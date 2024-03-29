package p2kotplot.types

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import p2kotplot.JsonToKotPlot
import p2kotplot.toClassName

data class ArrayType(val elementType: KotPlotType) : KotPlotType {
    override fun getNameAndCreate(
        converter: JsonToKotPlot,
        knownName: String?
    ): TypeName =
        "List".toClassName().parameterizedBy(elementType.getNameAndCreate(converter))
}