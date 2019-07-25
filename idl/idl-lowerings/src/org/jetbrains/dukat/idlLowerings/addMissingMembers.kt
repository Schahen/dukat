package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.*

private class MissingMemberResolver(val context: MissingMemberContext) : IDLLowering {

    fun getAllInterfaceParents(declaration: IDLInterfaceDeclaration): List<IDLInterfaceDeclaration> {
        val firstLevelParents = declaration.parents.mapNotNull { context.resolveInterface(it.name) }
        return firstLevelParents + firstLevelParents.flatMap { getAllInterfaceParents(it) }
    }

    fun getAllDictionaryParents(declaration: IDLDictionaryDeclaration): List<IDLDictionaryDeclaration> {
        val firstLevelParents = declaration.parents.mapNotNull { context.resolveDictionary(it.name) }
        return firstLevelParents + firstLevelParents.flatMap { getAllDictionaryParents(it) }
    }

    fun IDLTypeDeclaration.isOverriding(parentType: IDLTypeDeclaration): Boolean {
        return this == parentType
    }

    fun IDLDictionaryMemberDeclaration.isOverriding(parentMember: IDLDictionaryMemberDeclaration): Boolean {
        return name == parentMember.name
    }

    fun IDLAttributeDeclaration.isOverriding(parentMember: IDLAttributeDeclaration): Boolean {
        return name == parentMember.name &&
                static == parentMember.static
    }

    fun IDLOperationDeclaration.isOverriding(parentMember: IDLOperationDeclaration): Boolean {
        return name == parentMember.name &&
                static == parentMember.static &&
                arguments.size == parentMember.arguments.size &&
                !arguments.zip(parentMember.arguments) { a, b -> a.type.isOverriding(b.type) }.all { it }
    }

    private fun IDLInterfaceDeclaration.isInterface(): Boolean {
        return IDLSimpleExtendedAttributeDeclaration("NoInterfaceObject") in extendedAttributes
    }

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        val allParents = getAllInterfaceParents(declaration)
        val newAttributes: MutableList<IDLAttributeDeclaration> = mutableListOf()
        val newOperations: MutableList<IDLOperationDeclaration> = mutableListOf()
        for (parent in allParents) {
            for (parentOperation in parent.operations) {
                if (parentOperation.static || parent.isInterface()) {
                    if (declaration.operations.none { it.isOverriding(parentOperation) }) {
                        newOperations += parentOperation
                    }
                }
            }
            for (parentAttribute in parent.attributes) {
                if (parentAttribute.static || parent.isInterface()) {
                    if (declaration.attributes.none { it.isOverriding(parentAttribute) }) {
                        newAttributes += parentAttribute
                    }
                }
            }
        }
        if (declaration.isInterface() ||
                (declaration.constructors.isEmpty() && declaration.primaryConstructor == null)) {
            newAttributes.removeIf { !it.static }
            newOperations.removeIf { !it.static }
        }
        return declaration.copy(
                attributes = declaration.attributes + newAttributes,
                operations = declaration.operations + newOperations
        )
    }

    override fun lowerDictionaryDeclaration(declaration: IDLDictionaryDeclaration): IDLDictionaryDeclaration {
        val allParents = getAllDictionaryParents(declaration)
        val newMembers: MutableList<IDLDictionaryMemberDeclaration> = mutableListOf()
        for (parent in allParents) {
            for (parentMember in parent.members) {
                if (declaration.members.none { it.isOverriding(parentMember) }) {
                    newMembers += parentMember
                }
            }
        }
        return declaration.copy(
                members = declaration.members + newMembers.map { it.copy(inherited = true) }
        )
    }
}

private class MissingMemberContext : IDLLowering {
    private val interfaces: MutableMap<String, IDLInterfaceDeclaration> = mutableMapOf()
    private val dictionaries: MutableMap<String, IDLDictionaryDeclaration> = mutableMapOf()

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        interfaces[declaration.name] = declaration
        return declaration
    }

    override fun lowerDictionaryDeclaration(declaration: IDLDictionaryDeclaration): IDLDictionaryDeclaration {
        dictionaries[declaration.name] = declaration
        return declaration
    }

    fun resolveInterface(name: String): IDLInterfaceDeclaration? = interfaces[name]

    fun resolveDictionary(name: String): IDLDictionaryDeclaration? = dictionaries[name]
}

fun IDLFileDeclaration.addMissingMembers(): IDLFileDeclaration {
    val context = MissingMemberContext()
    return MissingMemberResolver(context).lowerFileDeclaration(
            context.lowerFileDeclaration(this)
    )

}
