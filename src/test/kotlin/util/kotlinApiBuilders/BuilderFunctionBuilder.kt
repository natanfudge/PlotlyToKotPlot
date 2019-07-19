package util.kotlinApiBuilders

import p2kotplot.*
import p2kotplot.plotlytypes.withOneOfArrayParameterPrefix
import util.TestBuilder

@TestBuilder
class BuilderFunctionBuilder(private val forPrimitiveArray : Boolean) {
    private val parameters = mutableListOf<ParameterComponents>()

    fun parameter(name: String, type: String, isOptional: Boolean = false, documentation: String = "") {
        parameters.add(ParameterComponents(if(forPrimitiveArray) name.withOneOfArrayParameterPrefix() else name , type, isOptional, documentation))
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