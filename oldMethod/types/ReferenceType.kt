package p2kotplot.types

import com.squareup.kotlinpoet.TypeName
import p2kotplot.JsonToKotPlot
import p2kotplot.toClassName

data class ReferenceType(val name: String) : KotPlotType {
    override fun getNameAndCreate(
        converter: JsonToKotPlot,
        knownName: String?
    ): TypeName =
        if (name.startsWith("Partial<")) {
            name.removePrefix("Partial<").removeSuffix(">").toClassName().copy(nullable = true)
        } else {
            name.toTitleCase().toClassName()
        }
}