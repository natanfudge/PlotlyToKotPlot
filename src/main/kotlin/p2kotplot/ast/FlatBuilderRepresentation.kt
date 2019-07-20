package p2kotplot.ast

import p2kotplot.util.addIfNotIn
import p2kotplot.plotlytypes.toTitleCase

data class BuilderClass(val name: String)
data class BuilderFunction(
    val name: String,
    val inClass: String?,
    val isOptional: Boolean,
    // Can be null when adding a singular primitive of an array
    val builderNameOfConstructedType: String?,
    val isForArray: Boolean
)

data class BuilderParameter(
    val name: String,
    /**
     * This is always a primitive
     */
    val type: String,
    val optional: Boolean,
    val belongsToFunction: String,
    val overloadNum: Int,
    val inConstructorOfClass: String?,
    val inBuilderFunctionInClass: String?,
    val documentation: String,
    val isEnumType: Boolean
)

data class Enum(val name: String, val elements: List<EnumConstant>)

data class EnumConstant(
    val name: String,
    /**
     * If the typescript file had a literal 'abcd' name would be 'Abcd' to comply with kotlin standards
     * but originalName would be 'abcd' for serialization purposes
     */
    val originalName: String
)


fun String.toBuilderName() = toTitleCase() + "Builder"

const val StartingOverloadNum = 0
//const val NotTopLevelOrInConstructor = "#"
const val TopLevel = "#TopLevel"

data class FlatBuilderRepresentation(
    private val builderClasses: MutableList<BuilderClass>,
    private val builderFunctions: MutableList<BuilderFunction>,
    private val parameters: MutableList<BuilderParameter>,
    private val enums: MutableList<Enum>
) {
    fun addBuilderClass(name: String) = builderClasses.addIfNotIn(BuilderClass(name))
    fun addBuilderFunction(
        name: String,
        inClass: String?,
        isOptional: Boolean,
//        type: BuilderFunctionsType,
        builderNameOfConstructedType: String?,
        isForArray: Boolean
    ) = builderFunctions.addIfNotIn(
        BuilderFunction(
            name,
            inClass,
            isOptional, /*type,*/
            builderNameOfConstructedType,
            isForArray
        )
    )

    fun addParameter(
        name: String,
        type: String,
        optional: Boolean,
        belongsToFunction: String,
        overloadNum: Int = StartingOverloadNum,
        inConstructorOfClass: String?,
        inBuilderFunctionInClass: String?,
        documentation: String,
        isEnumType: Boolean
    ) = parameters.addIfNotIn(
        BuilderParameter(
            name,
            type,
            optional,
            belongsToFunction,
            overloadNum,
            inConstructorOfClass,
            inBuilderFunctionInClass,
            documentation,
            isEnumType
        )
    )

    fun getParametersOfFunction(functionName: String) =
        parameters.filter { it.belongsToFunction == functionName && it.overloadNum == StartingOverloadNum }


    fun addEnum(name: String, elements: List<EnumConstant>) = enums.addIfNotIn(Enum(name, elements))

    override fun toString() =
        "FlatBuilderRepresentation(\nbuilderClasses = [\n\t" + builderClasses.joinToString(",\n\t") +
                "\n],\nbuilderFunctions = [\n\t" + builderFunctions.joinToString(",\n\t") + "\n],\nparameters = [\n\t" +
                parameters.joinToString(",\n\t") + "\n]\n)"

    fun extractDataAtTheEndOfProcessing() =
        PublicFlatBuilderRepresentation(builderClasses, builderFunctions, parameters, enums)


}

data class PublicFlatBuilderRepresentation(
    val builderClasses: List<BuilderClass>,
    val builderFunctions: List<BuilderFunction>,
    val parameters: List<BuilderParameter>,
    val enums: List<Enum>
)