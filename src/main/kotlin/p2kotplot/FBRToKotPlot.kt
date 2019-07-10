package p2kotplot

import com.squareup.kotlinpoet.*
import kotlinx.serialization.json.JsonElement
import p2kotplot.ast.PublicFlatBuilderRepresentation
import java.io.File
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlinx.serialization.json.JsonObject
import p2kotplot.util.*

const val PackageName = ""
const val InitFunctionName = "init"
const val BuildFunctionName = "build"
const val JsonMapName = "jsonMap"
const val SingularOfArrayFunctionPrefix = "addOneOf"
const val DslMarkerAnnotationName = "Builder"

fun String.toClassName() = ClassName(packageName = PackageName, simpleName = this)

//private data class Parameter(val name: String, val type: String)

/**
 * creates:
 * class Foo(private val x : Bar, private val y : Baz, ...)
 */
private fun TypeSpec.Builder.privateValsPrimaryConstructor(parameters: List<ParameterComponents>) {
//    primaryConstructor {
//
//    }
    primaryConstructor {
        for (parameter in parameters) {
            addParameter(name = parameter.name, type = parameter.type.toClassName().copy(nullable = parameter.isOptional))
        }
    }

    for (parameter in parameters) {
        this.addProperty(name = parameter.name, type = parameter.type.toClassName().copy(nullable = parameter.isOptional)) {
            initializer(parameter.name)
            addModifiers(KModifier.PRIVATE)
        }
    }


}
//TODO: make it obvious that required builder functions are required somehow
//TODO: add @dslmarker
//
//const val SingularOfArrayPrefix = "addOneOf"
//
class AstToKotPlot(builder: PublicFlatBuilderRepresentation) {
    val file: FileSpec.Builder = FileSpec.builder(PackageName, "PlotlyTypes")

    private val builderClasses = builder.builderClasses
    private val builderFunctions = builder.builderFunctions
    private val parameters = builder.parameters


    //TODO: add root builder functions
    init {
        val file = file.apply {
            setIndentToTab()
            addImport("kotlinx.serialization.json", "JsonLiteral", "JsonArray")

            addDslMarkerAnnotation()

            for (builderClass in builderClasses) {
                addBuilderClass(BuilderAssembly(builder).assemble(builderClass))
            }

            for (topLevelBuilderFunction in builderFunctions.filter { it.inClass == null }) {
                addTopLevelBuilderFunction(BuilderAssembly(builder).assemble(topLevelBuilderFunction))
            }


        }.build()


        file.writeTo(File("src\\main\\kotlin"))
    }

    private fun FileSpec.Builder.setIndentToTab() = indent("\t")

    private fun FileSpec.Builder.addDslMarkerAnnotation() {
        addAnnotationClass(name = DslMarkerAnnotationName) {
            addAnnotation(DslMarker::class)
        }
    }

    private fun FileSpec.Builder.addBuilderClass(classComponents: BuilderClassComponents) {
        addClass(className = classComponents.name) {
            privateValsPrimaryConstructor(classComponents.constructorArguments)
            addAnnotation(DslMarkerAnnotationName.toClassName())

            addProperty(
                name = JsonMapName,
                type = "MutableMap".toClassName().parameterizedBy(
                    "String".toClassName(),
                    JsonElement::class.asTypeName()
                )
            ) {
                initializer("mutableMapOf()")

                addModifiers(KModifier.PRIVATE)
            }

            for (arrayFieldName in classComponents.arrayFields) {
                addArrayField(arrayFieldName)
            }

            for (functionComponents in classComponents.builderFunctions) {
                addClassBuilderFunction(functionComponents)
            }

            addBuildFunction(classComponents.applyStatements)

        }
    }

    private fun TypeSpec.Builder.addArrayField(arrayFieldName: String) {
        addProperty(
            name = arrayFieldName,
            type = "MutableList".toClassName().parameterizedBy(JsonElement::class.asTypeName())
        ) {
            initializer("mutableListOf()")

            addModifiers(KModifier.PRIVATE)
        }

    }

    private fun TypeSpec.Builder.addBuildFunction(applyStatements: List<String>) {
        addFunction(name = BuildFunctionName) {
            returns(returnType = JsonObject::class)
            addModifiers(KModifier.INTERNAL)
            addCode(buildCodeBlock {
                addStatement("$JsonMapName.apply {")
                indent()
                for (statement in applyStatements) {
                    addStatement(statement)
                }
                unindent()
                addStatement("}")

                addStatement("return JsonObject($JsonMapName)")

            })
        }
    }

    private fun TypeSpec.Builder.addClassBuilderFunction(functionComponents: BuilderFunctionComponents) {
        addFunction(name = functionComponents.name) {
            addBuilderFunctionBody(functionComponents)
        }
    }

    private fun FileSpec.Builder.addTopLevelBuilderFunction(functionComponents: BuilderFunctionComponents) {
        addFunction(name = functionComponents.name) {
            addBuilderFunctionBody(functionComponents)
        }
    }

    private fun FunSpec.Builder.addBuilderFunctionBody(functionComponents: BuilderFunctionComponents) {
        for (param in functionComponents.parameters) {
            addParameter(name = param.name, type = param.type.toClassName().copy(nullable = param.isOptional)) {
                if (param.isOptional) defaultValue("null")
            }
        }
        // Add "init" receiver parameter at the end
        if (functionComponents.hasInitParam) {
            addParameter(
                name = InitFunctionName,
                type = LambdaTypeName.get(
                    receiver = functionComponents.builderNameOfConstructedType.toClassName(),
                    returnType = UNIT
                )
            ) {
                defaultValue("{}")
            }
        }


        addStatement(functionComponents.body)
    }


}

