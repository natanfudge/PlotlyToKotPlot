package p2kotplot

import com.squareup.kotlinpoet.*
import java.io.File
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import p2kotplot.ast.BuildStatementType
import p2kotplot.ast.Builder
import p2kotplot.ast.BuilderType

const val PackageName = ""
const val InitFunctionName = "init"
const val BuildFunctionName = "build"
const val JsonMapName = "jsonMap"

private data class Parameter(val name: String, val type: String)

/**
 * creates:
 * class Foo(private val x : Bar, private val y : Baz, ...)
 */
private fun TypeSpec.Builder.privateValsPrimaryConstructor(parameters: List<Parameter>) {
    primaryConstructor {
        for (parameter in parameters) {
            addParameter(name = parameter.name, type = parameter.type.toClassName().copy(nullable = true))
        }
    }

    for (parameter in parameters) {
        this.addProperty(name = parameter.name, type = parameter.type.toClassName().copy(nullable = true)) {
            initializer(parameter.name)
            addModifiers(KModifier.PRIVATE)
        }
    }


}


class AstToKotPlot(private val builderList: List<Builder>) {
    val file: FileSpec.Builder = FileSpec.builder(PackageName, "PlotlyTypes")

    init {
        val file = file.apply {
            indent("\t")
            addImport("kotlinx.serialization.json", "JsonLiteral")

            for (builder in builderList) {
                addBuilder(builder)
            }

        }.build()


        file.writeTo(File("src\\main\\kotlin"))
    }

//    private fun addBuilderClass(builderClassName: String, dsl: DslBuilderGroup) {
//        addClass(className = builderClassName) {
//            //TODO: use the "primary constructor list" instead
//            privateValsPrimaryConstructor(dsl.dslBuilder.parameters.map { Parameter(it.name,it.type) })
//
//            addProperty(
//                name = JsonMapName,
//                type = "MutableMap".toClassName().parameterizedBy(
//                    "String".toClassName(),
//                    JsonElement::class.asTypeName()
//                )
//            ) {
//                initializer("mutableMapOf()")
//            }
//
//            addBuilderFunctions(dsl)
//
//            addBuildFunction(dsl)
//
//        }
//    }


    private fun addBuilder(builder: Builder, parentClass: TypeSpec.Builder? = null) {
        //TODO: expand to additional builder functions

        val builderFunctionName = builder.builderFunctions[0].functionName
        if (parentClass == null) {
            addFunction(name = builderFunctionName) {
                addBuilderFunction(builder)
            }
        } else {
            parentClass.addFunction(name = builderFunctionName) {
                addBuilderFunction(builder)
            }
        }


        addBuilderClass(builder)
    }
    //TODO: add @dslmarker

    private fun FunSpec.Builder.addBuilderFunction(builder: Builder) {
//        addFunction(name = builder.builderFunctions[0].functionName) {
        //TODO: handle all builder functions
        for (param in builder.builderFunctions[0].parameters) {
            addParameter(name = param.name, type = param.type.toClassName().copy(nullable = true)) {
                defaultValue("null")
            }
        }
        // Add "init" receiver parameter at the end
        addParameter(
            name = InitFunctionName,
            type = LambdaTypeName.get(receiver = builder.builderClass.name.toClassName(), returnType = UNIT)
        ){
            defaultValue("{}")
        }

        val builderConstructorParams =
            "(" + builder.builderClass.constructorParameters.joinToString(", ") { it.name } + ")"

        val builderFunctionParams =
            "(" + builder.builderFunctions[0].parameters.joinToString(", ") { "\"${it.name}\" to JsonLiteral(${it.name})" } + ")"
        when (builder.builderType) {
            //TODO: remove apply(init) when not neccasiry
            BuilderType.Root -> addStatement("val jsonObject = ${builder.builderClass.name}$builderConstructorParams.apply($InitFunctionName).$BuildFunctionName()")

            BuilderType.Reference -> addStatement("$JsonMapName[\"${builder.name()}\"] = ${builder.builderClass.name}$builderConstructorParams.apply($InitFunctionName).$BuildFunctionName()")
            BuilderType.Array -> addStatement("${builder.name()}.add(${builder.builderFunctions[0].parameters[0].name})")
        }


    }

    private fun Builder.name() = this.builderFunctions[0].functionName

    private fun addBuilderClass(builder: Builder) {
        addClass(className = builder.builderClass.name) {
            privateValsPrimaryConstructor(builder.builderClass.constructorParameters.map {
                Parameter(
                    it.name,
                    it.type
                )
            })

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

            addSubBuilders(builder)
//
            addBuildFunction(builder)

        }
    }

    private fun TypeSpec.Builder.addSubBuilders(builder: Builder) {
        for (subBuilder in builder.builderClass.builders) {

            addBuilder(subBuilder, parentClass = this)
//            //                val buildFunctionName = if(buildFunction.type == DslBuilderType.Object) buildFunction.name else "addOneOf${buildFunction.name}"
//            this.addFunction(name = builderFunction.name) {
//
//                for (param in builderFunction.params) {
//                    addParameter(name = param.name, type = param.type.toClassName())
//                }
//                if (builderFunction.type == DslBuilderType.Object) {
//                    val builderFunctionParams =
//                        "(" + builderFunction.params.joinToString(", ") { "\"${it.name}\" to JsonLiteral(${it.name})" } + ")"
//                    addStatement("$JsonMapName[\"${builderFunction.name}\"] = JsonObject(mapOf$builderFunctionParams)")
//                } else {
//                    addStatement("$builderFunction.add(${builderFunction.params[0].name})")
//                }
//
//
//            }
        }
    }

    private fun TypeSpec.Builder.addBuildFunction(builder: Builder) {
        addFunction(name = BuildFunctionName) {
            returns(returnType = JsonObject::class)
            addCode(buildCodeBlock {
                addStatement("$JsonMapName.apply {")
                indent()
                for (statement in builder.builderClass.buildStatements) {
                    if (statement.builderType == BuildStatementType.Primitive) {
                        val variableName = statement.variableName
                        addStatement("if($variableName != null) jsonMap[\"$variableName\"] = JsonLiteral($variableName)")
                    }

                }
                unindent()
                addStatement("}")

                addStatement("return JsonObject($JsonMapName)")

            })
        }
    }
}

fun String.toClassName() = ClassName(packageName = PackageName, simpleName = this)
//
//@Suppress("MemberVisibilityCanBePrivate")
//class JsonToKotPlot(declarationFile: DeclarationFile) {
//    val file: FileSpec.Builder = FileSpec.builder(PackageName, "PlotlyTypes")
//    val types : List<KotPlotType>
//
//
//    init {
//        val file = file.apply {
//            val some = declarationFile.interfaces.subList(0, 50)
//            addInterfaces(some)
//            addTypeAliases(declarationFile.typeAliases)
////            addConstants(declarationFile.constants)
////            addFunctions(declarationFile.functions)
//        }.build()
//
//
//        file.writeTo(File("src\\main\\kotlin"))
//
//    }
//
////    fun inline
//
//
////    fun addTypeAliases(typeAliases: List<TypeAlias>) {
////        for (typeAlias in typeAliases) {
////            typeAlias.type.getNameAndCreate(knownName = typeAlias.name, converter = this)
////        }
////    }
//
//    fun addConstants(constants: List<Constant>) {
//        //TODO
//    }
//
//    fun addFunctions(functions: List<FunctionSignature>) {
//        //TODO
//    }
//
//    fun FileSpec.Builder.addInterfaces(interfaces: List<Interface>) {
//        for (interfaceDec in interfaces) {
//            addDataClass(
//                className = interfaceDec.name,
//                signatures = interfaceDec.props,
//                documentation = interfaceDec.documentation
//            )
//        }
//    }
//
////TODO: use only the filespec one, type should not use the typespec to add new type most of the time. (it causes it to make inner classes)
//
////    fun addDataClass(className: String, signatures: List<Signature>, documentation: String) {
////        addClass(className = className) {
////            if (signatures.filterIsInstance<PropertySignature>().isNotEmpty()) {
////                addModifiers(KModifier.DATA)
////                addSignatures(signatures, typeBuilder = this)
////            }
////            addAnnotation(Serializable::class)
////            addKdoc(documentation)
////
////        }
////    }
//
////
////    fun addSignatures(signatures: List<Signature>, typeBuilder: TypeSpec.Builder) {
////        typeBuilder.primaryConstructor {
////            for (signature in signatures) {
////                when (signature) {
////                    is FunctionSignature -> typeBuilder.addMethodSignature(signature)
////                    is PropertySignature -> typeBuilder.addPropertySignature(signature, funspecBuilder = this)
////                }
////            }
////
////        }
////
////    }
//
//    fun List<Signature>.getKDoc(): String {
//        return this.joinToString("\n") {
//            "[${it.name}]: ${it.documentation}"
//        }
//    }
//
//    fun TypeSpec.Builder.addMethodSignature(signature: FunctionSignature) {
//        //TODO: think how to add methods
//    }
//
//
//    fun TypeSpec.Builder.addParameter(parameter: Parameter) {
//
//    }
//
//
////    fun getParameterSpec(parameter: Parameter): ParameterSpec = parameter(
////        name = parameter.name,
////        type = parameter.type.getNameAndCreate(converter = this)
////    )
////
////
////    fun TypeSpec.Builder.addPropertySignature(
////        signature: PropertySignature,
////        funspecBuilder: FunSpec.Builder
////    ) {
////
////
////        //TODO: change from adding properties to adding primary constructor properties
////        funspecBuilder.addParameter(
////            name = signature.name, type = signature.type.getNameAndCreate(
////                converter = this@JsonToKotPlot
////            )
////        )
////        addProperty(
////            name = signature.name, type = signature.type.getNameAndCreate(
////                converter = this@JsonToKotPlot
////            )
////        ) {
////            initializer(signature.name)
////            addKdoc(signature.documentation)
////        }
////    }
//
//
//}