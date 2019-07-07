package p2kotplot.plotlytypes

import com.squareup.kotlinpoet.TypeName
//import p2kotplot.JsonToKotPlot
//import p2kotplot.addClass
//import p2kotplot.toClassName

//TODO: change this to something that better describes type literals
var typeLiteralCount = 1

/**
 * E.g. { thing : "value", otherThing : {...} }
 */
data class TypeLiteral(val nestedProperties: List<PropertySignature>) : KotPlotType {
    override fun emit(context: TypeContext) {
        TODO("not implemented")
    }
//    override fun getNameAndCreate(
//        converter: JsonToKotPlot,
//        knownName: String?
//    ): TypeName {
//        val typeLiteralName = "TypeLiteralOf" + nestedProperties.joinToString("And") { it.name.toTitleCase() }
//        converter.addClass(className = knownName ?: typeLiteralName) {
//            //            this.addsig
//            converter.addSignatures(nestedProperties, typeBuilder = this)
////            for (property in nestedProperties) {
////                addPropertySignature(property)
////            }
//        }
//        return typeLiteralName.toClassName()
//    }
}