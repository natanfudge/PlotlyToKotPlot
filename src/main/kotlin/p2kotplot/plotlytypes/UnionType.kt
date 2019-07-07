package p2kotplot.plotlytypes

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import p2kotplot.TypeContext

//import p2kotplot.JsonToKotPlot
//import p2kotplot.addClass
//import p2kotplot.toClassName

data class UnionType(val types: List<KotPlotType>) : KotPlotType {
    override fun emit(context: TypeContext) {
        TODO("not implemented")
    }
//    override fun getNameAndCreate(
//        converter: JsonToKotPlot,
//        knownName: String?
//    ): TypeName {
//
//        val literals = types.filterIsInstance<LiteralType>()
//        val references = types.filter { it !is LiteralType }
//
//
//        //TODO: when we get some literals and some references , we convert the literals into objects and the references into classes,
//        //TODO: and return a sealed class that is the objects or the classes.
//        //TODO: but if we get only literals, we return an enum class.
//
//        // If there are actual type in the union type the only way to handle it would be a sealed class where
//        // each reference is a subclass of the sealed class and then all the literals are also a enum as a subclass.
//        if (references.isNotEmpty()) {
//            val typeNames = mutableListOf<String>()
//
//            val sealedClassName = references.joinToString { reference ->
//                reference.getNameAndCreate(converter).getRepresentativeName().also { typeNames.add(it) }
//            }
//
//            //TODO: think about adding the subclasses as an inner class of the sealed class
//
//            // Add sealed class
//            converter.addClass(className = knownName?: sealedClassName) {
//                addModifiers(KModifier.SEALED)
//            }
//
//            // Add subclasses of sealed class
//            for (typeName in typeNames) {
//                converter.addClass(className = typeName) {
//                    superclass(sealedClassName.toClassName())
//                }
//            }
//
//            return sealedClassName.toClassName()
//        } else {
//            return literals.getNameAndCreate(converter)
//        }
//
//
//        //TODO: handle duplication of union type
//        //TODO: handle serialization of union/sealed type
//
//
//    }
}