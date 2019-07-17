package p2kotplot

import com.squareup.kotlinpoet.*
import kotlinx.serialization.json.JsonElement
import java.io.File
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.sun.org.apache.xpath.internal.operations.Bool
import kotlinx.serialization.json.JsonObject
import p2kotplot.KotlinWriter.Companion.PackageName
import p2kotplot.ast.Enum
import p2kotplot.plotlytypes.getArrayTypeElementName
import p2kotplot.plotlytypes.hasArrayTypePrefix
import p2kotplot.util.*
import java.sql.Types


fun String.toClassName() = ClassName(packageName = PackageName, simpleName = this)

fun String.toTypeNameWithArrayCheck(): TypeName {
    return if (this.hasArrayTypePrefix()) {
        "List".toClassName().parameterizedBy(this.getArrayTypeElementName().toClassName())
    } else toClassName()
}


/**
 * creates:
 * class Foo(private val x : Bar, private val y : Baz, ...)
 */
private fun TypeSpec.Builder.valsPrimaryConstructor(parameters: List<ParameterComponents>, private: Boolean) {
    primaryConstructor {
        for (parameter in parameters) {
            addParameter(
                name = parameter.name,
                type = parameter.type.toTypeNameWithArrayCheck().copy(nullable = parameter.isOptional)
            )
        }
    }

    for (parameter in parameters) {
        this.addProperty(
            name = parameter.name,
            type = parameter.type.toTypeNameWithArrayCheck().copy(nullable = parameter.isOptional)
        ) {
            initializer(parameter.name)
            if (private) addModifiers(KModifier.PRIVATE)
        }
    }
}

//TODO: make it obvious that required builder functions are required somehow

class KotlinWriter(val kotlinApi: KotlinApi, targetFileName: String) {
    val file: FileSpec.Builder = FileSpec.builder(PackageName, targetFileName)

//    private val builderClasses = builder.builderClasses
//    private val builderFunctions = builder.builderFunctions
//    private val enums = builder.enums

    fun writeTo(folder: File) {
        val file = file.apply {

            setIndentToTab()
            addImport("kotlinx.serialization.json", "JsonLiteral", "JsonArray")

            addDslMarkerAnnotation()

//            val kotlinApi = BuilderAssembly(builder).assembleAll()

            for (builderClass in kotlinApi.builderClasses) {
                addBuilderClass(builderClass)
            }

            for (topLevelBuilderFunction in kotlinApi.topLevelFunctions) {
                addTopLevelBuilderFunction(topLevelBuilderFunction)
            }

            for (enum in kotlinApi.enums) {
                addEnum(enum)
            }


        }.build()

        file.writeTo(folder)
    }


    private fun FileSpec.Builder.setIndentToTab() = indent("\t")

    private fun FileSpec.Builder.addDslMarkerAnnotation() {
        addAnnotationClass(name = DslMarkerAnnotationName) {
            addAnnotation(DslMarker::class)
        }
    }

    private fun FileSpec.Builder.addBuilderClass(classComponents: BuilderClassComponents) {
        addClass(className = classComponents.name) {
            valsPrimaryConstructor(classComponents.constructorArguments, private = true)
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

            for (arrayFieldName in classComponents.arrayFieldNames) {
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
            returns(JsonObject::class)
            addBuilderFunctionBody(functionComponents)
        }
    }

    private fun FunSpec.Builder.addBuilderFunctionBody(functionComponents: BuilderFunctionComponents) {
        for (param in functionComponents.parameters) {
            addParameter(
                name = param.name,
                type = param.type.toTypeNameWithArrayCheck().copy(nullable = param.isOptional)
            ) {
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

        //TODO: change documentation from per-function to per-parameter
//        addKdoc(functionComponents.documentation)

        addStatement(functionComponents.body)
    }

    private fun addEnum(enum: Enum) {
        file.addEnum(name = enum.name) {
            valsPrimaryConstructor(
                listOf(
                    ParameterComponents(
                        name = EnumOriginalName,
                        type = "String",
                        isOptional = false,
                        documentation = ""
                    )
                ), private = false
            )
            for (constant in enum.elements) {
                addEnumConstant(name = constant.name) {
                    addSuperclassConstructorParameter("%S", constant.originalName)
                }
            }
        }
    }
//TODO: put parameter docs on the parameters instead of the functions

    companion object {
        const val PackageName = ""
        const val InitFunctionName = "init"
        const val BuildFunctionName = "build"
        const val JsonMapName = "jsonMap"
        const val SingularOfArrayFunctionPrefix = "addOneOf"
        const val DslMarkerAnnotationName = "Builder"
        const val EnumOriginalName = "originalName"
    }
}

