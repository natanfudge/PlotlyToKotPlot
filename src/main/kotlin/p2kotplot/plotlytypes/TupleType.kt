package p2kotplot.plotlytypes

import p2kotplot.ast.FlatBuilderRepresentation
import p2kotplot.ast.TypeData

data class TupleType(val tupleTypes: List<KotPlotType>) : KotPlotType {
    override fun add(
        builder: FlatBuilderRepresentation,
        typeData: TypeData,
        builderClassIn: String?,
        nameAsParameter: String,
        isOptional: Boolean,
        functionAppearsIn: String,
        documentationAsParameter: String,
        isPartial: Boolean
    ) {
        ArrayType(elementType = ReferenceType("Any")).add(
            builder,
            typeData,
            builderClassIn,
            nameAsParameter,
            isOptional,
            functionAppearsIn,
            documentationAsParameter
        )
    }
}