package p2kotplot.plotlytypes

import p2kotplot.ast.FlatBuilderRepresentation
import p2kotplot.ast.TypeData

data class TupleType(val tupleTypes: List<KotPlotType>) : KotPlotType {
    override fun add(
        builder: FlatBuilderRepresentation,
        typeData: TypeData,
        builderFunctionInClass: String?,
        nameAsParameter: String,
        isOptional: Boolean,
        functionAppearsIn: String,
        documentationAsParameter: String,
        isPartial: Boolean,
        overloadNum: Int,
        paramInConstructorOfClass: String?,
        showInConstructor: Boolean,
        isForArray: Boolean
    ) {
        ArrayType(elementType = ReferenceType("Any")).add(
            builder,
            typeData,
            builderFunctionInClass,
            nameAsParameter,
            isOptional,
            functionAppearsIn,
            documentationAsParameter,
            overloadNum = overloadNum,
            paramInConstructorOfClass =builderFunctionInClass,
            isForArray = false
        )
    }
}