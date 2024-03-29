package p2kotplot.types

import com.squareup.kotlinpoet.TypeName
import p2kotplot.JsonToKotPlot
import p2kotplot.toClassName

data class IntersectionType(val types: List<KotPlotType>) : KotPlotType {
    override fun getNameAndCreate(
        converter: JsonToKotPlot,
        knownName: String?
    ): TypeName {
        val typeNames = mutableListOf<String>()
        return types.joinToString { type ->
            type.getNameAndCreate(converter).getRepresentativeName().also { typeNames.add(it) }
        }.also { intersectionTypeName ->
            converter.addDataClass(
                className = knownName ?: intersectionTypeName,
                documentation = "",
                //TODO: consider inlining the generated classes in this case
                signatures = typeNames.map { typeName ->
                    PropertySignature(
                        name = typeName.toLowerCase(),
                        type = ReferenceType(typeName),
                        documentation = ""
                    )
                })

        }.toClassName()

    }

}