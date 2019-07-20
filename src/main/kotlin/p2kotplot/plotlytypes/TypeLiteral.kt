package p2kotplot.plotlytypes

import p2kotplot.ast.FlatBuilderRepresentation
import p2kotplot.ast.TypeData
import p2kotplot.ast.toBuilderName

//import p2kotplot.JsonToKotPlot
//import p2kotplot.util.addClass
//import p2kotplot.toClassName

//TODO: change this to something that better describes type literals
var typeLiteralCount = 1

/**
 * E.g. { thing : "value", otherThing : {...} }
 */
data class TypeLiteral(val nestedProperties: List<PropertySignature>) : KotPlotType {
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
        showInConstructor: Boolean,
        isForArray: Boolean
    ) {
        // Type literals are anonymous so we give them an arbitrary name.
        val inventedTypeName = "TypeLiteralNum${typeLiteralCount++}"
        builder.addBuilderClass(name = inventedTypeName.toBuilderName())
        builder.addBuilderFunction(
            name = nameAsParameter,
            inClass = builderFunctionInClass,
//                type = BuilderFunctionsType.Reference,
            builderNameOfConstructedType = inventedTypeName.toBuilderName(),
            isOptional = isOptional,
            isForArray = isForArray
        )
        nestedProperties.addTypes(
            builder,
            typeData,
            builderClassIn = inventedTypeName.toBuilderName(),
            isPartial = isPartial,
            functionAppearsIn = nameAsParameter,
            overloadNum = overloadNum
        )
//            for (prop in nestedProperties) {
//                prop.type.add(
//                    builder = builder,
//                    typeData = typeData,
//                    builderFunctionInClass = inventedTypeName.toBuilderName(),
//                    nameAsParameter = prop.name,
//                    isOptional = prop.optional,
//                    functionAppearsIn = nameAsParameter,
//                    documentationAsParameter = prop.documentation
//                )
//            }
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