package p2kotplot.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import p2kotplot.plotlytypes.*
import kotlin.reflect.KClass

object GsonTest {
    val gson = createGson()
}

fun createGson(): Gson {
    val signatureTypeFactory = RuntimeTypeAdapterFactory
        .of(Signature::class.java, "signatureType")
        .registerSubtype(PropertySignature::class.java)
        .registerSubtype(FunctionSignature::class.java)

    val kotPlotTypeTypeFactory = RuntimeTypeAdapterFactory
        .of(KotPlotType::class.java, "kotPlotTypeType")
        .registerSubtype(LiteralType::class.java)
        .registerSubtype(UnionType::class.java)
        .registerSubtype(ReferenceType::class.java)
        .registerSubtype(FunctionType::class.java)
        .registerSubtype(TupleType::class.java)
        .registerSubtype(ArrayType::class.java)
        .registerSubtype(TypeLiteral::class.java)
        .registerSubtype(IntersectionType::class.java)
        .registerSubtype(ParameterizedType::class.java)

    return GsonBuilder().registerTypeAdapterFactory(signatureTypeFactory)
        .registerTypeAdapterFactory(kotPlotTypeTypeFactory)
        .create()
}


//@ExperimentalStdlibApi
//inline fun <reified T> String.parseTo(): T {
//    return GsonTest.gson.fromJson(this, T::class.java)
//}


//@ExperimentalStdlibApi
//inline fun <reified T> T.stringify(): String {
//    return GsonTest.gson.toJson(this)
//}
//
//fun <T> KClass<Array<T>>.parseList(s: String): List<T> = listOf(*GsonTest.gson.fromJson(s, this.java))