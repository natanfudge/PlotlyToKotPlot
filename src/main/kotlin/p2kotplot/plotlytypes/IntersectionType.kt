package p2kotplot.plotlytypes

import p2kotplot.ast.FlatBuilderRepresentation
import p2kotplot.ast.TypeData


/*
 * type Foo = ErrorOptions & {
	type: 'constant' | 'percent',
	value: number,
	valueminus?: number
};
*
 * }
 */

data class IntersectionType(val types: List<KotPlotType>) : KotPlotType {
    override fun add(
        builder: FlatBuilderRepresentation,
        typeData: TypeData,
        builderClassIn: String?,
        nameAsParameter: String,
        isOptional: Boolean,
        functionAppearsIn: String,
        documentationAsParameter: String,
        isPartial: Boolean,
        overloadNum: Int
    ) {
        //   Handled manually because it's easier to do it manually...

    }


}