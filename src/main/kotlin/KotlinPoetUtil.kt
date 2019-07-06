import com.squareup.kotlinpoet.*
import kotlin.reflect.KClass

fun file(packageName: String, fileName: String, init: FileSpec.Builder.() -> Unit): FileSpec =
    FileSpec.builder(packageName, fileName).apply(init).build()


fun classType(className: String, init: TypeSpec.Builder.() -> Unit): TypeSpec =
    TypeSpec.classBuilder(className).apply(init).build()

fun TypeSpec.Builder.addClass(className: String, init: TypeSpec.Builder.() -> Unit){
    addType(classType(className, init))
}

fun JsonToKotPlot.addClass(className: String, init: TypeSpec.Builder.() -> Unit){
    fileBuilder.addType(classType(className, init))
}

fun TypeSpec.Builder.classType(className: String, init: TypeSpec.Builder.() -> Unit): TypeSpec =
    TypeSpec.classBuilder(className).apply(init).build()

fun constructor(init: FunSpec.Builder.() -> Unit): FunSpec =
    FunSpec.constructorBuilder().apply(init).build()

fun TypeSpec.Builder.property(
    name: String,
    type: KClass<*>,
    vararg modifiers: KModifier,
    init: PropertySpec.Builder.() -> Unit
): PropertySpec =
    PropertySpec.builder(name, type, *modifiers).apply(init).build()

fun TypeSpec.Builder.addProperty(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    init: PropertySpec.Builder.() -> Unit = {}
){
    addProperty(PropertySpec.builder(name, type, *modifiers).apply(init).build())
}


//fun TypeSpec.Builder.addProperty()


fun TypeSpec.Builder.function(functionName: String, init: FunSpec.Builder.() -> Unit): FunSpec =
    FunSpec.builder(functionName).apply(init).build()

fun parameter(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    init: ParameterSpec.Builder.() -> Unit = {}
): ParameterSpec = ParameterSpec.builder(name, type, *modifiers).apply(init).build()

fun JsonToKotPlot.addObject(name: String, init : TypeSpec.Builder.() -> Unit = {}){
    fileBuilder.addType(TypeSpec.objectBuilder(name).apply(init).build())
}

fun JsonToKotPlot.addEnum(name: String, init: TypeSpec.Builder.() -> Unit){
    fileBuilder.addType(TypeSpec.enumBuilder(name).apply(init).build())
}

fun TypeSpec.Builder.primaryConstructor(init: FunSpec.Builder.() -> Unit){
    primaryConstructor(FunSpec.constructorBuilder().apply(init).build())
}