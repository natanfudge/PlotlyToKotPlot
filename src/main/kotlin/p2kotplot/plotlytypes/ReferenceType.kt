package p2kotplot.plotlytypes

import p2kotplot.ast.MutableBuilderTree
import sun.plugin.dom.exception.InvalidStateException

fun String.builderClassName() = this.toTitleCase() + "Builder"

data class ReferenceType(val name: String) : KotPlotType {
    override fun emit(tree: MutableBuilderTree, builderName: String) =
        when (this.name) {
            "number", "string" -> emitValueType(tree,builderName)
            else -> emitReferenceType(tree,builderName)
        }


    private fun emitValueType(tree: MutableBuilderTree, builderName: String) {
        tree.addPrimitiveParameter(type = name.toTitleCase() , name = builderName)
    }

    private fun emitReferenceType(tree: MutableBuilderTree, builderName: String) {
        val typeDeclaration = tree.findType(this.name)
        tree.addReferenceBuilder(builderName) {
            for (prop in typeDeclaration.props) {
                if (prop !is PropertySignature) throw InvalidStateException("Did not expect to find an interface with methods")
                prop.type.emit(this, prop.name)
            }
        }
    }
}