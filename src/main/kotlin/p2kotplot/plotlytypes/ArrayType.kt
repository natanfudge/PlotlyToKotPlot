package p2kotplot.plotlytypes

import p2kotplot.ast.FlatBuilderRepresentation
import p2kotplot.ast.NotTopLevelOrInConstructor
import p2kotplot.ast.TypeData

//import p2kotplot.JsonToKotPlot
//import p2kotplot.toClassName
private const val OneOfArrayMarker = "[ONE_OF_ARRAY_MARKER]"
private const val OneOfArrayParameterPrefix = "oneOf"

fun String.isBuilderFunctionNameForOneOfArray() = this.startsWith(OneOfArrayMarker)
fun String.getArrayBuilderFunctionOriginalName() = this.removePrefix(OneOfArrayMarker)

val String.arrParamName get() = OneOfArrayParameterPrefix + this.toTitleCase()

//fun String.toArrayParameterName() = OneOfArrayParameterPrefix + this.toTitleCase()



data class ArrayType(val elementType: KotPlotType) : KotPlotType {
    private fun String.toArrayBuilderFunctionName() = OneOfArrayMarker + this
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
        val arrayBuilderFunctionName = nameAsParameter.toArrayBuilderFunctionName()
        if (elementType is ReferenceType && elementType.isPrimitive()) {
            builder.addBuilderFunction(
                name = arrayBuilderFunctionName,
                inClass = builderClassIn,
                builderNameOfConstructedType = null,
                isOptional = isOptional
            )
            builder.addParameter(
                name = nameAsParameter.arrParamName,
                type = elementType.typeName,
                documentation = documentationAsParameter,
                isEnumType = false,
                paramInConstructorOfClass = NotTopLevelOrInConstructor,
                optional = false,
                belongsToFunction = arrayBuilderFunctionName
            )
        } else {
            elementType.add(
                builder = builder,
                typeData = typeData,
                builderClassIn = builderClassIn,
                nameAsParameter = arrayBuilderFunctionName,
                isOptional = false,
                functionAppearsIn = arrayBuilderFunctionName,
                documentationAsParameter = documentationAsParameter
            )
        }
//        builder.addBuilderClass(name = name.toBuilderName())
//            builder.addBuilderFunction(
//                name = nameAsParameter.toBuilderFunctionName(),
//                inClass = builderClassIn,
//                type = BuilderFunctionsType.Array
//            )
//            for (prop in typeData.findTypeProps(name)) {

//            }
    }

//    override fun getNameAndCreate(
//        converter: JsonToKotPlot,
//        knownName: String?
//    ): TypeName =
//        "List".toClassName().parameterizedBy(elementType.getNameAndCreate(converter))
}