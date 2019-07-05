import com.squareup.kotlinpoet.*
import kotlin.reflect.KClass

fun file(packageName: String, fileName: String, init: FileSpec.Builder.() -> Unit): FileSpec =
    FileSpec.builder(packageName, fileName).apply(init).build()


fun FileSpec.Builder.classType(className: String, init: TypeSpec.Builder.() -> Unit): TypeSpec =
    TypeSpec.classBuilder(className).apply(init).build()

fun TypeSpec.Builder.classType(className: String, init: TypeSpec.Builder.() -> Unit): TypeSpec =
    TypeSpec.classBuilder(className).apply(init).build()

fun TypeSpec.Builder.constructor(init: FunSpec.Builder.() -> Unit): FunSpec =
    FunSpec.constructorBuilder().apply(init).build()

fun TypeSpec.Builder.property(
    name: String,
    type: KClass<*>,
    vararg modifiers: KModifier,
    init: PropertySpec.Builder.() -> Unit
): PropertySpec =
    PropertySpec.builder(name, type, *modifiers).apply(init).build()

fun TypeSpec.Builder.property(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    init: PropertySpec.Builder.() -> Unit = {}
): PropertySpec =
    PropertySpec.builder(name, type, *modifiers).apply(init).build()


fun TypeSpec.Builder.function(functionName: String, init: FunSpec.Builder.() -> Unit): FunSpec =
    FunSpec.builder(functionName).apply(init).build()

fun TypeSpec.Builder.parameter(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    init: ParameterSpec.Builder.() -> Unit = {}
): ParameterSpec = ParameterSpec.builder(name, type, *modifiers).apply(init).build()