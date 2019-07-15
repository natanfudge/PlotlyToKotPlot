package p2kotplot.plotlytypes

import p2kotplot.ast.FlatBuilderRepresentation
import p2kotplot.ast.TypeData

//import p2kotplot.JsonToKotPlot
//import p2kotplot.toClassName
private const val OneOfArrayMarker = "[ONE_OF_ARRAY_MARKER]"
fun String.isBuilderFunctionNameForOneOfArray() = this.startsWith(OneOfArrayMarker)
fun String.getArrayBuilderFunctionOriginalName() = this.removePrefix(OneOfArrayMarker)

data class ArrayType(val elementType: KotPlotType) : KotPlotType {
    private fun String.toBuilderFunctionName() = OneOfArrayMarker + this
    //TODO: investigate if [BuilderFunction.type] is necessary
    override fun add(
        builder: FlatBuilderRepresentation,
        typeData: TypeData,
        builderClassIn: String?,
        nameAsParameter: String,
        isOptional: Boolean,
        functionAppearsIn: String,
        documentationAsParameter: String,
        isPartial: Boolean,
        overloadNum: Int
    ) {
//        builder.addBuilderClass(name = name.toBuilderName())
//            builder.addBuilderFunction(
//                name = nameAsParameter.toBuilderFunctionName(),
//                inClass = builderClassIn,
//                type = BuilderFunctionsType.Array
//            )
//            for (prop in typeData.findTypeProps(name)) {
                elementType.add(
                    builder = builder,
                    typeData = typeData,
                    builderClassIn = builderClassIn,
                    nameAsParameter = nameAsParameter.toBuilderFunctionName(),
                    isOptional = false,
                    functionAppearsIn = nameAsParameter.toBuilderFunctionName(),
                    documentationAsParameter = documentationAsParameter
                )
//            }
    }

//    override fun getNameAndCreate(
//        converter: JsonToKotPlot,
//        knownName: String?
//    ): TypeName =
//        "List".toClassName().parameterizedBy(elementType.getNameAndCreate(converter))
}