package p2kotplot.plotlytypes

import p2kotplot.ast.MutableBuilderTree

//import p2kotplot.JsonToKotPlot
//import p2kotplot.toClassName

data class ArrayType(val elementType: KotPlotType) : KotPlotType {
    override fun emit(context: MutableBuilderTree, builderName: String) {
        TODO("not implemented")
    }

//    override fun getNameAndCreate(
//        converter: JsonToKotPlot,
//        knownName: String?
//    ): TypeName =
//        "List".toClassName().parameterizedBy(elementType.getNameAndCreate(converter))
}