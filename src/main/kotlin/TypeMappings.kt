import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy


//fun LiteralType.getName(): TypeName {
//    //TODO: should actually return an enum
//    return this.literal.toClassName()
//}


fun String.toClassName() = ClassName(packageName = PackageName, simpleName = this)


//fun UnionType.getName(builder: TypeSpec.Builder): TypeName {
//    //TODO: when we get some literals and some references , we convert the literal part into an enum,
//    //TODO: and return a sealed class that is the reference type, or the enum.
//    //TODO: but if we get only literals, we return the enum class purely.
//    val typeNames = mutableListOf<String>()
//
//    val sealedClassName = this.types.joinToString { type ->
//        val typeName = builder.getTypeNameAndAddIfNeeded(type)
//        if (typeName is ClassName) {
//            typeName.simpleName.also { typeNames.add(it) }
//        } else {
//            TODO("Not sure how to handle the non-ClassName case")
//        }
//
//    }
//
//    // Add sealed class
//    builder.addType(builder.classType(className = sealedClassName) {
//        addModifiers(KModifier.SEALED)
//    })
//
//    // Add subclasses of sealed class
//    for (typeName in typeNames) {
//        builder.addType(builder.classType(className = typeName) {
//            superclass(sealedClassName.toClassName())
//        })
//    }
//
//    //TODO: handle duplication of union type
//    //TODO: handle serialization of union/sealed types
//
//
//    return sealedClassName.toClassName()
//
//}

//fun ReferenceType.getName(): TypeName {
//    return this.name.toClassName()
//}

//fun FunctionType.getName(builder: TypeSpec.Builder): LambdaTypeName {
//    return LambdaTypeName.get(
//        receiver = null,
//        parameters = this.parameters.map { builder.getParameterSpec(it) },
//        returnType = builder.getTypeNameAndAddIfNeeded(this.returnType)
//    )
//}

fun TypeSpec.Builder.getParameterSpec(parameter: Parameter): ParameterSpec = parameter(
    name = parameter.name,
    type = parameter.type.getName(builder = this)
)

//TODO: convert into object oriented functions


//fun TypeSpec.Builder.getTupleTypeName(tupleType: TupleType): ParameterizedTypeName {
//    val typeNames = tupleType.tupleTypes.map { getTypeNameAndAddIfNeeded(it) }
//
//    return "List".toClassName().parameterizedBy(*typeNames.toTypedArray())
//
//}

//fun TypeSpec.Builder.getArrayTypeName(arrayType: ArrayType): TypeName {
//    return "List".toClassName().parameterizedBy(getTypeNameAndAddIfNeeded(arrayType.elementType))
//}

//TODO: change this to something that better describes type literals
//var typeLiteralCount = 1
//
//fun TypeSpec.Builder.getTypeLiteralNameAndAddType(typeLiteral: TypeLiteral): TypeName {
//    val typeLiteralName = "TypeLiteral#${typeLiteralCount++}"
//    addType(classType(className = typeLiteralName) {
//        for (property in typeLiteral.nestedProperties) {
//            addPropertySignature(property)
//        }
//    })
//    return typeLiteralName.toClassName()
//}
