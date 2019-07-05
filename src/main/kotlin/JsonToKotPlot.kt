import com.squareup.kotlinpoet.*
import java.io.File
import java.io.Serializable


@ExperimentalStdlibApi
fun main() {
    val plotlyTypesFile = File("src/ts/plotlyTypes.json")
    val plotlyTypes = plotlyTypesFile.readText()
    val deserialized = Array<Interface>::class.parseList(plotlyTypes)

    val some = deserialized.subList(0, 10)
//    println(some)

    createKotlinCode(some)


}

const val PackageName = "kotplot.internal.models"

//    val arr = gson.fromJson(s, clazz)
//    return listOf(*arr) //or return for a one-liner
//}

fun createKotlinCode(interfaces: List<Interface>): String {

    val file = file(PackageName, "PlotlyTypes") {
        for (interfaceDec in interfaces) {
            addType(
                classType(className = interfaceDec.name) {
                    addModifiers(KModifier.DATA)
                    addAnnotation(Serializable::class)
                    addKdoc(interfaceDec.documentation)
                    addSignatures(interfaceDec.props)
                }
            )
        }
    }

    file.writeTo(System.out)

    return ""
}

fun TypeSpec.Builder.addSignatures(signatures: List<Signature>) {
    for (signature in signatures) {
        when (signature) {
            is MethodSignature -> addMethodSignature(signature)
            is PropertySignature -> addPropertySignature(signature)
        }

    }
}

fun TypeSpec.Builder.addMethodSignature(signature: MethodSignature) {
//    addFunction(function(functionName = signature.name) {
//        addComment(signature.documentation)
//        returns(returnType = getTypeNameAndAddIfNeeded(signature.returnType))
//        for (parameter in signature.parameters) {
//            addParameter(parameter)
//        }
//    })
    //TODO: think how to add methods
}

//fun TypeSpec.Builder.getTypeNameAndAddIfNeeded(type: KotPlotType): TypeName {
//    return  when (type) {
//            is LiteralType -> type.getName()
//            is UnionType -> this.getName(type)
//            is ReferenceType -> this.getName(type)
//            is FunctionType -> this.getName(type)
//            is TupleType -> getTupleTypeName(type)
//            is ArrayType -> getArrayTypeName(type)
//            is TypeLiteral -> getTypeLiteralNameAndAddType(type)
//        }
//
//}

fun TypeSpec.Builder.addParameter(parameter: Parameter) {

}


fun TypeSpec.Builder.addPropertySignature(signature: PropertySignature) {
    //TODO: change from adding properties to adding primary constructor properties
    addProperty(property(name = signature.name,type = signature.type.getName(builder = this)){
        addKdoc(signature.documentation)
    })
}


