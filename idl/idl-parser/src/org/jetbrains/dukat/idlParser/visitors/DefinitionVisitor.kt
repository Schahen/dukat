package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLOperationDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTopLevelDeclaration
import org.jetbrains.dukat.idlParser.getName

internal class DefinitionVisitor : WebIDLBaseVisitor<IDLTopLevelDeclaration>() {
    private var name: String = ""
    private val myAttributes: MutableList<IDLAttributeDeclaration> = mutableListOf()
    private val operations: MutableList<IDLOperationDeclaration> = mutableListOf()

    override fun defaultResult(): IDLTopLevelDeclaration {
        return IDLInterfaceDeclaration(name, myAttributes, operations)
    }

    override fun visitAttributeRest(ctx: WebIDLParser.AttributeRestContext): IDLTopLevelDeclaration {
        myAttributes.add(with(AttributeVisitor()) {
            visit(ctx)
            visitAttributeRest(ctx)
        })
        return defaultResult()
    }

    override fun visitInterface_(ctx: WebIDLParser.Interface_Context): IDLTopLevelDeclaration {
        name = ctx.getName()
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitOperation(ctx: WebIDLParser.OperationContext): IDLTopLevelDeclaration {
        operations.add(OperationVisitor().visit(ctx))
        return defaultResult()
    }
}
