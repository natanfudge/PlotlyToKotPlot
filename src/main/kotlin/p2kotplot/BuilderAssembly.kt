package p2kotplot

import p2kotplot.ast.BuilderClass
import p2kotplot.ast.BuilderFunction
import p2kotplot.ast.PublicFlatBuilderRepresentation
import p2kotplot.plotlytypes.getArrayBuilderFunctionOriginalName
import p2kotplot.plotlytypes.isBuilderFunctionNameForOneOfArray
import p2kotplot.plotlytypes.toTitleCase

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
    val builderNameOfConstructedType : String
)


data class ParameterComponents(
    val name: String,
    val type: String
)

class BuilderAssembly(builder: PublicFlatBuilderRepresentation) {
//    private val builderClasses = builder.builderClasses
    private val builderFunctions = builder.builderFunctions
    private val parameters = builder.parameters

    fun assemble(builderClass: BuilderClass): BuilderClassComponents {
        val name = builderClass.name

        val builderFunctions = builderClass.getBuilderFunctions().map { function ->
            BuilderFunctionComponents(
                name = function.getFinalName(),
                parameters = function.getParameters().map {
                    ParameterComponents(
                        name = it.name,
                        type = it.type
                    )
                },
                body = function.getBody(),
                builderNameOfConstructedType = function.builderNameOfConstructedType
            )
        }

        val arrayFields =
            builderClass.getBuilderFunctions().filter { it.name.isBuilderFunctionNameForOneOfArray() }.map {
                it.name.getArrayBuilderFunctionOriginalName()
            }

        val applyStatements = builderClass.getConstructorArguments().map {
            "if(${it.name} != null) Map.add(${it.name})"
        } + builderClass.getBuilderFunctions().filter {
            it.name.isBuilderFunctionNameForOneOfArray()
        }.map { builderFunction ->
            builderFunction.name.getArrayBuilderFunctionOriginalName().let {
                "if($it.isNotEmpty()) Map.add($it)"
            }
        }

        val constructorArguments = builderClass.getConstructorArguments().map {
            ParameterComponents(
                name = it.name,
                type = it.type
            )
        }

        return BuilderClassComponents(name, builderFunctions, arrayFields, applyStatements, constructorArguments)
    }

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

    private fun BuilderFunction.getBody(): String {
        val builderConstructorParams =
            "(" + this.getParameters().joinToString(", ") { it.name } + ")"
        return if (this.name.isBuilderFunctionNameForOneOfArray()) {
            val originalName = this.name.getArrayBuilderFunctionOriginalName()
            "$originalName.add($builderNameOfConstructedType$builderConstructorParams.apply($InitFunctionName).$BuildFunctionName()"
        } else {
            "$JsonMapName[\"${this.name}\" = $builderNameOfConstructedType$builderConstructorParams.apply($InitFunctionName).$BuildFunctionName()"
        }
    }
}

