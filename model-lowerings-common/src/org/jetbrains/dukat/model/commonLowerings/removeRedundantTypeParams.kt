package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.ownerContext.wrap

private fun TypeModel.collectTypeParams(): List<NameEntity> {
    return when (this) {
        is TypeParameterReferenceModel -> listOf(this.name)
        is TypeValueModel -> params.flatMap { it.type.collectTypeParams() }
        is FunctionTypeModel -> parameters.flatMap { it.type.collectTypeParams() } + type.collectTypeParams()
        is TypeParameterModel -> type.collectTypeParams()
        else -> emptyList()
    }
}

class RemoveRedundantTypeParamsLowering: TopLevelModelLowering {

    private fun getTypeParamsDependencies(typeParam: NameEntity, allTypeParams: List<TypeParameterModel>): List<NameEntity> {
        return allTypeParams.find { (it.type as? TypeValueModel)?.value == typeParam }?.constraints?.flatMap {
            it.collectTypeParams()
        }.orEmpty()
    }

    private fun getUsedTypeParams(methodModel: MethodModel, alreadyKnownUsed: List<NameEntity>): List<NameEntity> {
        val dependencies = alreadyKnownUsed.flatMap { getTypeParamsDependencies(it, methodModel.typeParameters) }.distinct()
        if (alreadyKnownUsed.containsAll(dependencies)) {
            return alreadyKnownUsed
        }
        return getUsedTypeParams(methodModel, (alreadyKnownUsed + dependencies).distinct())
    }

    private fun getUsedTypeParams(methodModel: MethodModel): List<NameEntity> {
        val fromTypes = (methodModel.parameters.flatMap { it.type.collectTypeParams() } + methodModel.type.collectTypeParams()).distinct()
        return getUsedTypeParams(methodModel, fromTypes)
    }

    private fun lowerMethodModel(ownerContext: NodeOwner<MethodModel>): MethodModel {
        val methodModel = ownerContext.node

        val usedTypeParams = getUsedTypeParams(methodModel)
        val typeParamsResolved = methodModel.typeParameters.filter { typeParameterModel ->
            val type = typeParameterModel.type
            if (type is TypeValueModel) {
                usedTypeParams.contains(type.value)
            } else {
                true
            }
        }

        return methodModel.copy(typeParameters = typeParamsResolved)
    }

    @Suppress("UNCHECKED_CAST")
    fun lowerMemberModel(ownerContext: NodeOwner<MemberModel>): MemberModel {
        return when(ownerContext.node) {
            is MethodModel -> lowerMethodModel(ownerContext as NodeOwner<MethodModel>)
            else -> ownerContext.node
        }
    }

    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel {
        return super.lowerInterfaceModel(ownerContext.copy(node = ownerContext.node.copy(
            members = ownerContext.node.members.map { member -> lowerMemberModel(ownerContext.wrap(member)) }
        )), parentModule)
    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel {
        return super.lowerClassModel(ownerContext.copy(node = ownerContext.node.copy(
                members = ownerContext.node.members.map { member -> lowerMemberModel(ownerContext.wrap(member)) }
        )), parentModule)
    }
}

class RemoveRedundantTypeParams : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return RemoveRedundantTypeParamsLowering().lowerRoot(module, NodeOwner(module, null))
    }
}