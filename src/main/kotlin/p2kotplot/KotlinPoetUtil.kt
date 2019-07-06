package p2kotplot

import com.squareup.kotlinpoet.*

//fun file(packageName: String, fileName: String, init: FileSpec.Builder.() -> Unit): FileSpec =
//    FileSpec.builder(packageName, fileName).apply(init).build()


//fun classType(className: String, init: TypeSpec.Builder.() -> Unit): TypeSpec =


//inline fun TypeSpec.Builder.p2kotplot.addClass(className: String, init: TypeSpec.Builder.() -> Unit){
//    addType(classType(className, init))
//}

inline fun JsonToKotPlot.addClass(className: String, init: TypeSpec.Builder.() -> Unit){
    fileBuilder.addType(TypeSpec.classBuilder(className).apply(init).build())
}

//inline fun TypeSpec.Builder.classType(className: String, init: TypeSpec.Builder.() -> Unit): TypeSpec =
//    TypeSpec.classBuilder(className).apply(init).build()
//
//inline fun constructor(init: FunSpec.Builder.() -> Unit): FunSpec =
//    FunSpec.constructorBuilder().apply(init).build()
//
//inline fun TypeSpec.Builder.property(
//    name: String,
//    type: KClass<*>,
//    vararg modifiers: KModifier,
//    init: PropertySpec.Builder.() -> Unit
//): PropertySpec =
//    PropertySpec.builder(name, type, *modifiers).apply(init).build()

inline fun TypeSpec.Builder.addProperty(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    init: PropertySpec.Builder.() -> Unit = {}
){
    addProperty(PropertySpec.builder(name, type, *modifiers).apply(init).build())
}


//fun TypeSpec.Builder.p2kotplot.addProperty()


//inline fun TypeSpec.Builder.function(functionName: String, init: FunSpec.Builder.() -> Unit): FunSpec =
//    FunSpec.builder(functionName).apply(init).build()

inline fun parameter(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    init: ParameterSpec.Builder.() -> Unit = {}
): ParameterSpec = ParameterSpec.builder(name, type, *modifiers).apply(init).build()

inline fun JsonToKotPlot.addObject(name: String, init : TypeSpec.Builder.() -> Unit = {}){
    fileBuilder.addType(TypeSpec.objectBuilder(name).apply(init).build())
}

inline fun JsonToKotPlot.addEnum(name: String, init: TypeSpec.Builder.() -> Unit){
    fileBuilder.addType(TypeSpec.enumBuilder(name).apply(init).build())
}

inline fun TypeSpec.Builder.primaryConstructor(init: FunSpec.Builder.() -> Unit){
    primaryConstructor(FunSpec.constructorBuilder().apply(init).build())
}