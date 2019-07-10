package p2kotplot.plotlytypes

import p2kotplot.ast.FlatBuilderRepresentation
import p2kotplot.ast.TypeData

//import p2kotplot.addObject
//import com.squareup.kotlinpoet.TypeName
//import p2kotplot.JsonToKotPlot
//import p2kotplot.addEnum
//import p2kotplot.toClassName




/**
 * E.g. "thing1"
 */
data class LiteralType(val literal: String) : KotPlotType {



    //TODO fix upper/ lower casing
    //TODO serialize properly
//    override fun getNameAndCreate(
//        converter: JsonToKotPlot,
//        knownName: String?
//    ): TypeName =
//        getLiteralName().also { converter.addObject(it) }.toClassName()
//
    fun getLiteralName(): String = literal.toTitleCase()

}

///**
// * Create an enum type of all the literals and return its name
// */
//fun Iterable<LiteralType>.getNameAndCreate(converter: JsonToKotPlot): TypeName =
//    this.joinToString("Or") { it.getLiteralName() }.also {
//        converter.addEnum(it) {
//            for (literalType in this@getNameAndCreate) {
//                addEnumConstant(literalType.getLiteralName())
//            }
//        }
//    }.toClassName()