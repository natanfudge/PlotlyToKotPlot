package p2kotplot.ast

import p2kotplot.util.addIfNotIn
import p2kotplot.plotlytypes.toTitleCase

data class BuilderClass(val name: String)
data class BuilderFunction(
    val name: String,
    val inClass: String?,
//    val type: BuilderFunctionsType,
    val builderNameOfConstructedType: String
)

data class BuilderParameter(
    val name: String,
    /**
     * This is always a primitive
     */
    val type: String,
    val belongsToFunction: String,
    val paramInConstructorOfClass: String?
)

enum class BuilderFunctionsType {
    Root,
    Reference,
    Array,
    Union
}

fun String.toBuilderName() = toTitleCase() + "Builder"

data class FlatBuilderRepresentation(
    private val builderClasses: MutableList<BuilderClass>,
    private val builderFunctions: MutableList<BuilderFunction>,
    private val parameters: MutableList<BuilderParameter>
) {
    fun addBuilderClass(name: String) = builderClasses.addIfNotIn(BuilderClass(name))
    fun addBuilderFunction(
        name: String,
        inClass: String?,
//        type: BuilderFunctionsType,
        builderNameOfConstructedType: String
    ) = builderFunctions.add(BuilderFunction(name, inClass, /*type,*/ builderNameOfConstructedType))

    fun addParameter(name: String, type: String, belongsToFunction: String, paramInConstructorOfClass: String?) =
        parameters.addIfNotIn(
            BuilderParameter(name, type, belongsToFunction, paramInConstructorOfClass)
        )

    override fun toString() =
        "FlatBuilderRepresentation(\nbuilderClasses = [\n\t" + builderClasses.joinToString(",\n\t") +
                "\n],\nbuilderFunctions = [\n\t" + builderFunctions.joinToString(",\n\t") + "\n],\nparameters = [\n\t" +
                parameters.joinToString(",\n\t") + "\n]\n)"

    fun extractDataAtTheEndOfProcessing() =
        PublicFlatBuilderRepresentation(builderClasses, builderFunctions, parameters)

//    operator fun component1() = builderClasses

}

data class PublicFlatBuilderRepresentation(
    val builderClasses: MutableList<BuilderClass>,
    val builderFunctions: MutableList<BuilderFunction>,
    val parameters: MutableList<BuilderParameter>
)