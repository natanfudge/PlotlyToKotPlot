import com.squareup.kotlinpoet.*
import kotlinx.serialization.Serializable
import java.io.File


//TODO: add "export type Datum = string | number | Date | null" to the plotlyToJSON
//TODO: add "export const Plots: StaticPlots;"  to the plotlyToJSON
//TODO convert the top 2 into kotlin
//TODO: fix Partial<> bug
//TODO: fix union types not working correctly
@ExperimentalStdlibApi
fun main() {
    val plotlyTypesFile = File("src/ts/plotlyTypes.json")
    val plotlyTypes = plotlyTypesFile.readText()
    val deserialized = Array<Interface>::class.parseList(plotlyTypes)

    val some = deserialized.subList(0, 50)

//    val helloWorld = classType(className = "HelloWorld") {
//        primaryConstructor {
//            addParameter("greeting", String::class)
//            addProperty(
//                property("greeting", String::class) {
//                    initializer("greeting")
//                }
//
//            )
//        }
//
//    }
//
//
//    print(helloWorld)

    createKotlinCode(some)


}


data class zz(val y: Int)

//@Serializable
//data class StaticPlots()

const val PackageName = ""

//TODO: map number to double
fun createKotlinCode(interfaces: List<Interface>): String {

    val file = file(PackageName, "PlotlyTypes") {
        for (interfaceDec in interfaces) {
            addClass(className = interfaceDec.name) {
                if (interfaceDec.props.filterIsInstance<PropertySignature>().isNotEmpty()) {
                    addModifiers(KModifier.DATA)
                    addSignatures(interfaceDec.props)
                }
                addAnnotation(Serializable::class)
                addKdoc(interfaceDec.documentation)

            }

        }
    }

    file.writeTo(File("src\\main\\kotlin"))

    return ""
}

fun TypeSpec.Builder.addSignatures(signatures: List<Signature>) {
    primaryConstructor {
        for (signature in signatures) {
            when (signature) {
                is FunctionSignature -> addMethodSignature(signature)
                is PropertySignature -> addPropertySignature(signature, funspecBuilder = this)
            }
        }

//        addKdoc(signatures.getKDoc())

    }

}

fun List<Signature>.getKDoc(): String {
    return this.joinToString("\n") {
        "[${it.name}]: ${it.documentation}"
    }

//    return """
//        ${this.map {it.name}}
//    """.trimIndent()
}

fun TypeSpec.Builder.addMethodSignature(signature: FunctionSignature) {
    //TODO: think how to add methods
}


fun TypeSpec.Builder.addParameter(parameter: Parameter) {

}

fun String.toClassName() = ClassName(packageName = PackageName, simpleName = this)


fun TypeSpec.Builder.getParameterSpec(parameter: Parameter): ParameterSpec = parameter(
    name = parameter.name,
    type = parameter.type.getName(builder = this)
)


fun TypeSpec.Builder.addPropertySignature(signature: PropertySignature, funspecBuilder: FunSpec.Builder) {
//    funspecBuilder
//        .addParameter("greeting", signature.type.getName(builder = this))
////        .build()
//
//    val helloWorld = TypeSpec.classBuilder("HelloWorld")
//        .primaryConstructor(flux)
//        .addProperty(
//            PropertySpec.builder("greeting", String::class)
//                .initializer("greeting")
//                .addModifiers(KModifier.PRIVATE)
//                .build()
//        )
//        .build()

    //TODO: change from adding properties to adding primary constructor properties
    funspecBuilder.addParameter(name = signature.name, type = signature.type.getName(builder = this))
    addProperty(name = signature.name, type = signature.type.getName(builder = this)) {
        initializer(signature.name)
        addKdoc(signature.documentation)
    }


//    {
//        addKdoc(signature.documentation)
//    })
}

//fun TypeSpec.Builder.addProperties


