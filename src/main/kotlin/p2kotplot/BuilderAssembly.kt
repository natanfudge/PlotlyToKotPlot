package p2kotplot

import p2kotplot.ast.BuilderClass
import p2kotplot.ast.BuilderFunction
import p2kotplot.ast.BuilderParameter
import p2kotplot.ast.PublicFlatBuilderRepresentation
import p2kotplot.plotlytypes.getArrayBuilderFunctionOriginalName
import p2kotplot.plotlytypes.isBuilderFunctionNameForOneOfArray
import p2kotplot.plotlytypes.toTitleCase
import sun.plugin.dom.exception.InvalidStateException

data class BuilderClassComponents(
    val name: String,
    val builderFunctions: List<BuilderFunctionComponents>,
    val arrayFields: List<String>,
    val applyStatements: List<String>,
    val constructorArguments: List<ParameterComponents>
)

data class BuilderFunctionComponents(
    val name: String,
    val parameters: List<ParameterComponents>,
    val body: String,
    val builderNameOfConstructedType: String,
    val hasInitParam : Boolean
)


data class ParameterComponents(
    val name: String,
    val type: String,
    val isOptional: Boolean
)

class BuilderAssembly(builder: PublicFlatBuilderRepresentation) {
    private val builderClasses = builder.builderClasses
    private val builderFunctions = builder.builderFunctions
    private val parameters = builder.parameters

    fun assemble(builderClass: BuilderClass): BuilderClassComponents {
        val name = builderClass.name

        val builderFunctions = builderClass.getBuilderFunctions().map { assemble(it) }

        val arrayFields =
            builderClass.getBuilderFunctions().filter { it.name.isBuilderFunctionNameForOneOfArray() }.map {
                it.name.getArrayBuilderFunctionOriginalName()
            }

        val applyStatements = builderClass.getConstructorArguments().map {
            val nullCheck = if(it.optional) "if(${it.name} != null)" else ""
            "$nullCheck $JsonMapName[\"${it.name}\"] = JsonLiteral(${it.name})"
        } + builderClass.getBuilderFunctions().filter {
            it.name.isBuilderFunctionNameForOneOfArray()
        }.map { builderFunction ->
            builderFunction.name.getArrayBuilderFunctionOriginalName().let {
                val isEmptyCheck = if(builderFunction.isOptional) "if($it.isNotEmpty())" else ""
                "$isEmptyCheck $JsonMapName[\"$it\"] = JsonArray($it)"
            }
        }

        val constructorArguments = builderClass.getConstructorArguments().map {
            it.toParameterComponents()
        }

        return BuilderClassComponents(name, builderFunctions, arrayFields, applyStatements, constructorArguments)
    }

    private fun BuilderParameter. toParameterComponents(): ParameterComponents {
        return ParameterComponents(
            name = name,
            type = type,
            isOptional = optional
        )
    }

    fun assemble(builderFunction: BuilderFunction) = BuilderFunctionComponents(
        name = builderFunction.getFinalName(),
        parameters = builderFunction.getParameters().map {
            it.toParameterComponents()
        },
        body = builderFunction.getBody(),
        builderNameOfConstructedType = builderFunction.builderNameOfConstructedType,
        hasInitParam = builderFunction.builderClassUsedInFunctionHasBuilderFunctions()
    )

    private fun BuilderClass.getConstructorArguments() = parameters.filter {
        it.paramInConstructorOfClass == name
    }

    private fun BuilderClass.getBuilderFunctions() = builderFunctions.filter {
        it.inClass == name
    }

    private fun BuilderFunction.getParameters() = parameters.filter {
        it.belongsToFunction == name
    }

    private fun BuilderFunction.getFinalName() = if (name.isBuilderFunctionNameForOneOfArray()) {
        SingularOfArrayFunctionPrefix + name.getArrayBuilderFunctionOriginalName().toTitleCase()
    } else name

    private fun BuilderFunction.getBuilderClassUsedInFunction() = builderClasses.find {
        it.name == this.builderNameOfConstructedType
    } ?: throw InvalidStateException("Could not find class $inClass")

    private fun BuilderFunction.builderClassUsedInFunctionHasBuilderFunctions() =
        this.getBuilderClassUsedInFunction().getBuilderFunctions().isNotEmpty()

    private fun BuilderFunction.getBody(): String {
        val builderConstructorParams =
            "(" + this.getParameters().joinToString(", ") { it.name } + ")"

        // If the builder class used has no builder functions there is no need for an init function.
        // This is also handled in the FBRToKotPlot side.
        val applyCall = if (builderClassUsedInFunctionHasBuilderFunctions()) ".apply($InitFunctionName)" else ""

        val objectConstruction =
            "$builderNameOfConstructedType$builderConstructorParams$applyCall.$BuildFunctionName()"

            return when {
                this.name.isBuilderFunctionNameForOneOfArray() -> {
                    val originalName = this.name.getArrayBuilderFunctionOriginalName()
                    "$originalName.add($objectConstruction)"
                }
                this.inClass != null -> "$JsonMapName[\"${this.name}\"] = $objectConstruction"
                else -> "val jsonObject = $objectConstruction;print(jsonObject.asIterable().joinToString(\"\\n\"))"
            }
    }
}

