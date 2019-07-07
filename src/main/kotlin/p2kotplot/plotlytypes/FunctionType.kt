package p2kotplot.plotlytypes

import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.TypeName
//import p2kotplot.JsonToKotPlot

data class FunctionType(val parameters: List<Parameter>, val returnType: KotPlotType) : KotPlotType {
    override fun emit(context: TypeContext) {
        TODO("not implemented")
    }

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

