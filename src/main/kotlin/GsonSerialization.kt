import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import kotlin.reflect.KClass

fun createGson() : Gson {
    val signatureTypeFactory = RuntimeTypeAdapterFactory
        .of(Signature::class.java, "signatureType")
        .registerSubtype(PropertySignature::class.java)
        .registerSubtype(MethodSignature::class.java)

    val kotPlotTypeTypeFactory = RuntimeTypeAdapterFactory
        .of(KotPlotType::class.java, "kotPlotTypeType")
        .registerSubtype(LiteralType::class.java)
        .registerSubtype(UnionType::class.java)
        .registerSubtype(ReferenceType::class.java)
        .registerSubtype(FunctionType::class.java)
        .registerSubtype(TupleType::class.java)
        .registerSubtype(ArrayType::class.java)
        .registerSubtype(TypeLiteral::class.java)

    return  GsonBuilder().registerTypeAdapterFactory(signatureTypeFactory)
        .registerTypeAdapterFactory(kotPlotTypeTypeFactory)
        .create()
}

val gson = createGson()

@ExperimentalStdlibApi
inline fun <reified T> String.parseTo(): T {
    return gson.fromJson(this, T::class.java)
}


@ExperimentalStdlibApi
inline fun <reified T> T.stringify(): String {
    return gson.toJson(this)
}

fun <T> KClass<Array<T>>.parseList(s: String): List<T>  = listOf(*gson.fromJson(s, this.java))