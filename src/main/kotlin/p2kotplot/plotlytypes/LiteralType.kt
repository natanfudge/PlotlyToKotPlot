package p2kotplot.plotlytypes

import p2kotplot.ast.FlatBuilderRepresentation
import p2kotplot.ast.TypeData



/**
 * E.g. "thing1"
 */
data class LiteralType(val literal: String) : KotPlotType {
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
        showInConstructor: Boolean
    ) {
        //This is only called when it's not as a union type, i.e. interface x { thing : "literal" }, this (almost) never happens.

    }

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