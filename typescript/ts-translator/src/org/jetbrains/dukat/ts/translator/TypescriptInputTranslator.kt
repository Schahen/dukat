package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.model.commonLowerings.addStandardImportsAndAnnotations
import org.jetbrains.dukat.model.commonLowerings.lowerOverrides
import org.jetbrains.dukat.commonLowerings.merge.mergeClassLikesAndModuleDeclarations
import org.jetbrains.dukat.commonLowerings.merge.mergeClassesAndInterfaces
import org.jetbrains.dukat.commonLowerings.merge.mergeModules
import org.jetbrains.dukat.commonLowerings.merge.mergeNestedClasses
import org.jetbrains.dukat.commonLowerings.merge.mergeVarsAndInterfaces
import org.jetbrains.dukat.commonLowerings.merge.mergeWithNameSpace
import org.jetbrains.dukat.commonLowerings.merge.specifyTypeNodesWithModuleData
import org.jetbrains.dukat.model.commonLowerings.omitStdLib
import org.jetbrains.dukat.compiler.lowerPrimitives
import org.jetbrains.dukat.model.commonLowerings.escapeIdentificators
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.nodeIntroduction.introduceNodes
import org.jetbrains.dukat.nodeIntroduction.introduceQualifiedNode
import org.jetbrains.dukat.nodeIntroduction.introduceTupleNodes
import org.jetbrains.dukat.nodeIntroduction.introduceTypeNodes
import org.jetbrains.dukat.nodeIntroduction.lowerIntersectionType
import org.jetbrains.dukat.nodeIntroduction.lowerThisType
import org.jetbrains.dukat.nodeIntroduction.lowerUnionType
import org.jetbrains.dukat.nodeIntroduction.resolveModuleAnnotations
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.tsLowerings.*
import org.jetbrains.dukat.tsmodel.SourceBundleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetrbains.dukat.nodeLowering.lowerings.introduceMissedOverloads
import org.jetrbains.dukat.nodeLowering.lowerings.introduceModels
import org.jetrbains.dukat.nodeLowering.lowerings.lowerNullable
import org.jetrbains.dukat.nodeLowering.lowerings.lowerVarargs
import org.jetrbains.dukat.nodeLowering.lowerings.moveTypeAliasesOutside
import org.jetrbains.dukat.nodeLowering.lowerings.rearrangeConstructors
import org.jetrbains.dukat.nodeLowering.lowerings.rearrangeGeneratedEntities
import org.jetrbains.dukat.nodeLowering.lowerings.specifyUnionType
import org.jetrbains.dukat.nodeLowering.lowerings.typeAlias.resolveTypeAliases


interface TypescriptInputTranslator<T> : InputTranslator<T> {
    val moduleNameResolver: ModuleNameResolver

    fun lower(sourceSet: SourceSetDeclaration): SourceSetModel {
        return sourceSet
                .filterOutNonDeclarations()
                .resolveTypescriptUtilityTypes()
                .resolveDefaultTypeParams()
                .generateInterfaceReferences()
                .eliminateStringType()
                .desugarArrayDeclarations()
                .cleanupDotNames()
                .introduceNodes(moduleNameResolver)
                .introduceTypeNodes()
                .introduceQualifiedNode()
                .introduceTupleNodes()
                .resolveModuleAnnotations()
                .lowerUnionType()
                .lowerNullable()
                .lowerPrimitives()
                .lowerVarargs()
                .lowerIntersectionType()
                .lowerThisType()
                .resolveTypeAliases()
                .specifyUnionType()
                .rearrangeGeneratedEntities()
                .rearrangeConstructors()
                .introduceMissedOverloads()
                .moveTypeAliasesOutside()
                .introduceModels()
                .escapeIdentificators()
                .mergeModules()
                .mergeWithNameSpace()
                .mergeClassesAndInterfaces()
                .mergeClassLikesAndModuleDeclarations()
                .mergeVarsAndInterfaces()
                .mergeNestedClasses()
                .lowerOverrides()
                .specifyTypeNodesWithModuleData()
                .addStandardImportsAndAnnotations()
                .omitStdLib()
    }

    fun lower(sourceBundle: SourceBundleDeclaration): SourceBundleModel {
        return SourceBundleModel(sourceBundle.sources.map { source -> lower(source) })
    }
}