package p2kotplot.plotlytypes

import com.squareup.kotlinpoet.TypeName
//import p2kotplot.JsonToKotPlot
//import p2kotplot.toClassName

//TODO: make this into a proper data class with "first", "second" etc as properties
data class TupleType(val tupleTypes: List<KotPlotType>) : KotPlotType {
    override fun emit(context: TypeContext) {
        TODO("not implemented")
    }
//    override fun getNameAndCreate(
//        converter: JsonToKotPlot,
//        knownName: String?
//    ): TypeName {
//        val typeNames = mutableListOf<String>()
//
//        val createdClassName = "TupleOf${tupleTypes.joinToString("And") { type ->
//            type.getNameAndCreate(converter).getRepresentativeName().also { typeNames.add(it) }
//        }}"
//
//
//        converter.addDataClass(
//            className = createdClassName, documentation = "",
//            signatures = typeNames.mapIndexed { i, type ->
//                PropertySignature(name = "property$i", documentation = "", type = ReferenceType(type))
//            }
//        )
//
//        return createdClassName.toClassName()
//
////        return "Tuple".toClassName().parameterizedBy(*typeNames.toTypedArray())
//    }
}