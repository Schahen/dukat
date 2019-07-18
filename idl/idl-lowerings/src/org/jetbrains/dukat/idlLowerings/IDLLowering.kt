package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.*


interface IDLLowering {

    fun lowerTypeDeclaration(declaration: IDLTypeDeclaration): IDLTypeDeclaration {
        return declaration.copy(typeParameter = declaration.typeParameter?.let { lowerTypeDeclaration(it) })
    }

    fun lowerAttributeDeclaration(declaration: IDLAttributeDeclaration): IDLAttributeDeclaration {
        return declaration.copy(type = lowerTypeDeclaration(declaration.type))
    }

    fun lowerArgumentDeclaration(declaration: IDLArgumentDeclaration): IDLArgumentDeclaration {
        return declaration.copy(type = lowerTypeDeclaration(declaration.type))
    }

    fun lowerOperationDeclaration(declaration: IDLOperationDeclaration): IDLOperationDeclaration {
        return declaration.copy(
                returnType = lowerTypeDeclaration(declaration.returnType),
                arguments = declaration.arguments.map { lowerArgumentDeclaration(it) }
        )
    }

    fun lowerConstructorDeclaration(declaration: IDLConstructorDeclaration): IDLConstructorDeclaration {
        return declaration.copy(arguments = declaration.arguments.map { lowerArgumentDeclaration(it) })
    }

    fun lowerTypedefDeclaration(declaration: IDLTypedefDeclaration): IDLTypedefDeclaration {
        return declaration
    }

    fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        return declaration.copy(
                attributes = declaration.attributes.map { lowerAttributeDeclaration(it) },
                operations = declaration.operations.map { lowerOperationDeclaration(it) },
                constructors = declaration.constructors.map { lowerConstructorDeclaration(it) },
                parents = declaration.parents.map { lowerTypeDeclaration(it) },
                primaryConstructor = if (declaration.primaryConstructor == null) {
                    null
                } else {
                    lowerConstructorDeclaration(declaration.primaryConstructor!!)
                }
        )
    }

    fun lowerTopLevelDeclaration(declaration: IDLTopLevelDeclaration): IDLTopLevelDeclaration {
        return when (declaration) {
            is IDLInterfaceDeclaration -> lowerInterfaceDeclaration(declaration)
            is IDLTypedefDeclaration -> lowerTypedefDeclaration(declaration)
            else -> declaration
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<IDLTopLevelDeclaration>): List<IDLTopLevelDeclaration> {
        return declarations.map { declaration ->
            lowerTopLevelDeclaration(declaration)
        }
    }

    fun lowerFileDeclaration(fileDeclaration: IDLFileDeclaration): IDLFileDeclaration {
        return fileDeclaration.copy(
                declarations = lowerTopLevelDeclarations(fileDeclaration.declarations)
        )
    }
}