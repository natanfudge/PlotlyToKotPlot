package p2kotplot.plotlytypes

import p2kotplot.ast.MutableBuilderTree
import sun.plugin.dom.exception.InvalidStateException

//import p2kotplot.JsonToKotPlot
//import p2kotplot.toClassName

data class ArrayType(val elementType: KotPlotType) : KotPlotType {
    override fun emit(tree: MutableBuilderTree, builderName: String) {
        if(elementType !is ReferenceType) throw InvalidStateException("Did not expect an array with a non-reference element type.")

        //TODO: 'inline' the reference into the current builder function, i.e.
        // instead of : '(init: AddOneOfPowderBuilder.() -> Unit = {})'
        // we have: '(num1 : Int, str1 : String, init: AddOneOfPowderBuilder.() -> Unit = {})'
//        val typeDeclaration = tree.findType(elementType.name)
        tree.addArrayBuilder(builderName,arrayGenericType = elementType.name){
            elementType.emit(this, builderName = builderName)
        }
    }

//    override fun getNameAndCreate(
//        converter: JsonToKotPlot,
//        knownName: String?
//    ): TypeName =
//        "List".toClassName().parameterizedBy(elementType.getNameAndCreate(converter))
}