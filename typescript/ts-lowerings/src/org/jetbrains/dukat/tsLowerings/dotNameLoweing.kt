package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.*
import org.jetbrains.dukat.tsmodel.types.*

class DotNameLowering : DeclarationLowering {
    override fun lowerVariableDeclaration(
        declaration: VariableDeclaration,
        owner: NodeOwner<ModuleDeclaration>
    ): VariableDeclaration = declaration

    override fun lowerFunctionDeclaration(
        declaration: FunctionDeclaration,
        owner: NodeOwner<FunctionOwnerDeclaration>
    ): FunctionDeclaration = declaration

    override fun lowerClassDeclaration(
        declaration: ClassDeclaration,
        owner: NodeOwner<ModuleDeclaration>
    ): ClassDeclaration {
        return declaration.copy(members = declaration.members.filterNot { (it as? PropertyDeclaration)?.name?.contains(".") == true })
    }

    override fun lowerInterfaceDeclaration(
        declaration: InterfaceDeclaration,
        owner: NodeOwner<ModuleDeclaration>
    ): InterfaceDeclaration {
        return declaration.copy(members = declaration.members.filterNot { (it as? PropertyDeclaration)?.name?.contains(".") == true })
    }

    override fun lowerGeneratedInterfaceDeclaration(
        declaration: GeneratedInterfaceDeclaration,
        owner: NodeOwner<ModuleDeclaration>
    ): GeneratedInterfaceDeclaration {
        return declaration.copy(members = declaration.members.filterNot { (it as? PropertyDeclaration)?.name?.contains(".") == true })
    }

    override fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration {
        return declaration
    }

    override fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration): FunctionTypeDeclaration {
        return declaration
    }

    override fun lowerParameterDeclaration(declaration: ParameterDeclaration): ParameterDeclaration {
        return declaration
    }

    override fun lowerTypeParameter(declaration: TypeParameterDeclaration): TypeParameterDeclaration {
        return declaration
    }

    override fun lowerUnionTypeDeclaration(declaration: UnionTypeDeclaration): UnionTypeDeclaration {
        return declaration
    }

    override fun lowerTupleDeclaration(declaration: TupleDeclaration): TupleDeclaration {
        return declaration
    }

    override fun lowerIntersectionTypeDeclaration(declaration: IntersectionTypeDeclaration): IntersectionTypeDeclaration {
        return declaration
    }

    override fun lowerMemberDeclaration(
        declaration: MemberEntity,
        owner: NodeOwner<ClassLikeDeclaration>
    ): MemberEntity {
        return declaration
    }

    override fun lowerMethodSignatureDeclaration(
        declaration: MethodSignatureDeclaration,
        owner: NodeOwner<MemberEntity>
    ): MethodSignatureDeclaration {
        return declaration
    }

    override fun lowerTypeAliasDeclaration(
        declaration: TypeAliasDeclaration,
        owner: NodeOwner<ModuleDeclaration>
    ): TypeAliasDeclaration {
        return declaration
    }

    override fun lowerIndexSignatureDeclaration(
        declaration: IndexSignatureDeclaration,
        owner: NodeOwner<MemberEntity>
    ): IndexSignatureDeclaration {
        return declaration
    }


}
fun ModuleDeclaration.cleanupDotNames() = DotNameLowering().lowerDocumentRoot(this)
fun SourceFileDeclaration.cleanupDotNames() = this.copy(root = root.cleanupDotNames())
fun SourceSetDeclaration.cleanupDotNames(): SourceSetDeclaration = this.copy(sources = sources.map(SourceFileDeclaration::cleanupDotNames))