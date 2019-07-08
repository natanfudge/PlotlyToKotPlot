package p2kotplot.plotlytypes

import p2kotplot.ast.MutableBuilderTree

//import p2kotplot.JsonToKotPlot
//import p2kotplot.toClassName

data class IntersectionType(val types: List<KotPlotType>) : KotPlotType {
    override fun emit(context: MutableBuilderTree, builderName: String) {
        TODO("not implemented")
    }

//    override fun getNameAndCreate(
//        converter: JsonToKotPlot,
//        knownName: String?
//    ): TypeName {
//        val typeNames = mutableListOf<String>()
//        return types.joinToString { type ->
//            type.getNameAndCreate(converter).getRepresentativeName().also { typeNames.add(it) }
//        }.also { intersectionTypeName ->
//            converter.addDataClass(
//                className = knownName ?: intersectionTypeName,
//                documentation = "",
//                //TODO: consider inlining the generated classes in this case
//                signatures = typeNames.map { typeName ->
//                    PropertySignature(
//                        name = typeName.toLowerCase(),
//                        type = ReferenceType(typeName),
//                        documentation = ""
//                    )
//                })
//
//        }.toClassName()
//
//    }

}