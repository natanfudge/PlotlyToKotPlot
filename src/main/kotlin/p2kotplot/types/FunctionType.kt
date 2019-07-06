package p2kotplot.types

import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.TypeName
import p2kotplot.JsonToKotPlot

data class FunctionType(val parameters: List<Parameter>, val returnType: KotPlotType) : KotPlotType {
    override fun getNameAndCreate(
        converter: JsonToKotPlot,
        knownName: String?
    ): TypeName =
        LambdaTypeName.get(
            receiver = null,
            parameters = this.parameters.map { converter.getParameterSpec(it) },
            returnType = returnType.getNameAndCreate(converter)
        )

}