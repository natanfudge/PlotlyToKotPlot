package util.kotlinApiBuilders

import p2kotplot.*
import p2kotplot.plotlytypes.toTitleCase
import util.TestBuilder

@TestBuilder
class BuilderClassBuilder {
    private val builderFunctions = mutableListOf<BuilderFunctionComponents>()
    private val arrayFields = mutableListOf<String>()
    private val applyStatements = mutableListOf<String>()
    private val constructorArguments = mutableListOf<ParameterComponents>()

    fun builderFunction(
        name: String,
        builderNameOfConstructedType: String,
        hasInitParam: Boolean,
        init: BuilderFunctionBuilder.() -> Unit
    ) {
        builderFunctions.add(
            BuilderFunctionBuilder().apply(init).build(
                name,
                builderNameOfConstructedType,
                hasInitParam,
                BuilderFunctionType.DataClass
            )
        )
    }

    fun arrayBuilderFunction(
        arrayName: String,
        constructedTypeBuilderName: String?,
        hasInitParam: Boolean,
        isOptional: Boolean = false,
        init: BuilderFunctionBuilder.() -> Unit
    ) {
        builderFunctions.add(
            BuilderFunctionBuilder().apply(init).build(
                name = KotlinWriter.SingularOfArrayFunctionPrefix + arrayName.toTitleCase(),
                builderNameOfConstructedType =  constructedTypeBuilderName,
                hasInitParam = hasInitParam,
                functionType =  BuilderFunctionType.Array,
                arrayName = arrayName
            )
        )
        arrayField(arrayName)
        arrayApplyStatement(variableName = arrayName, variableIsOptional = isOptional)
    }

    private fun arrayField(arrayName: String) {
        arrayFields.add(arrayName)
    }

    private fun applyStatement(
        variableName: String,
        variableIsOptional: Boolean = false,
        variableIsAny: Boolean = false,
        variableIsEnum: Boolean = false
    ) {
        applyStatements.add(
            applyStatementString(
                variableIsOptional,
                isForArray = false,
                variableName = variableName,
                variableIsAny = variableIsAny,
                variableIsEnum = variableIsEnum
            )
        )
    }

    private fun arrayApplyStatement(variableName: String, variableIsOptional: Boolean = false) {
        applyStatements.add(
            applyStatementString(
                variableIsOptional,
                isForArray = true,
                variableName = variableName,
                variableIsAny = false,
                variableIsEnum = false
            )
        )
    }

    fun constructorArgument(
        name: String,
        type: String,
        isOptional: Boolean = false,
        isEnum: Boolean = false,
        documentation: String = ""
    ) {
        constructorArguments.add(ParameterComponents(name, type, isOptional, documentation))
        applyStatement(name, isOptional, variableIsAny = type == "Any", variableIsEnum = isEnum)
    }


    fun build(name: String): BuilderClassComponents = BuilderClassComponents(
        name,
        builderFunctions,
        arrayFields,
        applyStatements,
        constructorArguments
    )

}