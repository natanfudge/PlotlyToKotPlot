package util.kotlinApiBuilders

import p2kotplot.*
import util.TestBuilder

@TestBuilder
class BuilderFunctionBuilder {
    private val parameters = mutableListOf<ParameterComponents>()

    fun parameter(name: String, type: String, isOptional: Boolean = false, documentation: String = "") {
        parameters.add(ParameterComponents(name, type, isOptional, documentation))
    }

    fun build(
        name: String,
//        documentation: String,
        builderNameOfConstructedType: String?,
        hasInitParam: Boolean,
        functionType: BuilderFunctionType,
        arrayName: String? = null
    ): BuilderFunctionComponents = BuilderFunctionComponents(
        name,
//        documentation,
        parameters,
        body = when (functionType) {
            BuilderFunctionType.TopLevel -> topLevelFunctionBody(
                builderNameOfConstructedType = builderNameOfConstructedType!!,
                withApplyCall = hasInitParam,
                parameters = parameters
            )
            BuilderFunctionType.DataClass -> dataClassFunctionBody(
                builderNameOfConstructedType = builderNameOfConstructedType!!,
                withApplyCall = hasInitParam,
                parameters = parameters,
                builderFunctionName = name
            )
            BuilderFunctionType.Array -> oneOfArrayFunctionBody(
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