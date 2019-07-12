package p2kotplot.plotlytypes

import p2kotplot.ast.FlatBuilderRepresentation
import p2kotplot.ast.TypeData
import sun.plugin.dom.exception.InvalidStateException

//import p2kotplot.JsonToKotPlot

data class ParameterizedType(val name: String, val typeArguments: List<KotPlotType>) : KotPlotType {
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
        assert(typeArguments.size != 1) {"Only one type argument is expected"}
        when (name) {
            "Partial" -> typeArguments[0].add(
                builder,
                typeData,
                builderClassIn,
                nameAsParameter,
                isOptional,
                functionAppearsIn,
                documentationAsParameter,
                isPartial = true
            )
            "Array" -> ArrayType(typeArguments[0]).add(
                builder,
                typeData,
                builderClassIn,
                nameAsParameter,
                isOptional,
                functionAppearsIn,
                documentationAsParameter,
                isPartial
            )
            else -> throw InvalidStateException("Did not expect anything other than 'Array' or 'Partial'")
        }
    }
    //TODO: handle partial here


//    override fun getNameAndCreate(
//        converter: JsonToKotPlot,
//        knownName: String?
//    ): TypeName =
//        LambdaTypeName.get(
//            receiver = null,
//            parameters = this.parameters.map { converter.getParameterSpec(it) },
//            returnType = returnType.getNameAndCreate(converter)
//        )

}


//fun newPlot(root: Root, data: Data[], layout?: Partial<Layout>, config?: Partial<Config>): Promise<PlotlyHTMLElement>;
//fun newPlot(init: NewPlotBuilder.() -> Unit){
//    fun newPlot(root: Root, data: Data[], layout?: Partial<Layout>, config?: Partial<Config>): Promise<PlotlyHTMLElement>
//}

