package util

import p2kotplot.*
import p2kotplot.ast.Enum
import p2kotplot.ast.EnumConstant
import p2kotplot.ast.toBuilderName
import util.BuilderFunctionType.*

fun kotlinApi(init: KotlinApiBuilder.() -> Unit) = KotlinApiBuilder().apply(init).build()

@TestBuilder
class KotlinApiBuilder {
    private val builderClasses = mutableListOf<BuilderClassComponents>()
    private val topLevelFunctions = mutableListOf<BuilderFunctionComponents>()
    private val enums = mutableListOf<Enum>()

    fun builderClass(name: String, init: BuilderClassBuilder.() -> Unit) {
        builderClasses.add(BuilderClassBuilder().apply(init).build(name))
    }

    fun topLevelFunction(
        name: String,
//        documentation: String = "",
        hasInitParam: Boolean,
        init: BuilderFunctionBuilder.() -> Unit = {}
    ) {
        topLevelFunctions.add(
            BuilderFunctionBuilder().apply(init).build(
                name,
//                documentation,
                name.toBuilderName(),
                hasInitParam,
                functionType = TopLevel
            )
        )
    }

    fun enum(name: String, constants: EnumConstantsBuilder.() -> Unit) {
        enums.add(Enum(name, EnumConstantsBuilder().apply(constants).build()))
    }

    fun build() = KotlinApi(builderClasses, topLevelFunctions, enums)

}

@TestBuilder
class EnumConstantsBuilder {
    private val constants = mutableListOf<EnumConstant>()
    fun constant(name: String, originalName: String) {
        constants.add(EnumConstant(name, originalName))
    }

    fun build() = constants
}

enum class BuilderFunctionType {
    TopLevel,
    DataClass,
    Array
}

@TestBuilder
class BuilderFunctionBuilder {
    private val parameters = mutableListOf<ParameterComponents>()

    fun parameter(name: String, type: String, isOptional: Boolean = false, documentation: String = "") {
        parameters.add(ParameterComponents(name, type, isOptional,documentation))
    }

    fun build(
        name: String,
//        documentation: String,
        builderNameOfConstructedType: String,
        hasInitParam: Boolean,
        functionType: BuilderFunctionType,
        arrayName: String? = null
    ): BuilderFunctionComponents = BuilderFunctionComponents(
        name,
//        documentation,
        parameters,
        body = when (functionType) {
            TopLevel -> topLevelFunctionBody(
                builderNameOfConstructedType = builderNameOfConstructedType,
                withApplyCall = hasInitParam,
                parameters = parameters
            )
            DataClass -> dataClassFunctionBody(
                builderNameOfConstructedType = builderNameOfConstructedType,
                withApplyCall = hasInitParam,
                parameters = parameters,
                builderFunctionName = name
            )
            Array -> oneOfArrayFunctionBody(
                builderNameOfConstructedType = builderNameOfConstructedType,
                withApplyCall = hasInitParam,
                parameters = parameters,
                arrayName = arrayName!!
            )
        },
        builderNameOfConstructedType = builderNameOfConstructedType,
        hasInitParam = hasInitParam
    )

}

@TestBuilder
class BuilderClassBuilder {
    private val builderFunctions = mutableListOf<BuilderFunctionComponents>()
    private val arrayFields = mutableListOf<String>()
    private val applyStatements = mutableListOf<String>()
    private val constructorArguments = mutableListOf<ParameterComponents>()

    fun builderFunction(
        name: String,
//        documentation: String = "",
        builderNameOfConstructedType: String,
        hasInitParam: Boolean,
        init: BuilderFunctionBuilder.() -> Unit
    ) {
        builderFunctions.add(
            BuilderFunctionBuilder().apply(init).build(
                name,
//                documentation,
                builderNameOfConstructedType,
                hasInitParam,
                DataClass
            )
        )
    }

    fun arrayField(arrayName: String) {
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

    fun arrayApplyStatement(variableName: String, variableIsOptional: Boolean = false) {
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

    fun constructorArgument(name: String, type: String, isOptional: Boolean = false, isEnum: Boolean = false, documentation: String = "") {
        constructorArguments.add(ParameterComponents(name, type, isOptional,documentation))
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