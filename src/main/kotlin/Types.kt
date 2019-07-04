import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import kotlinx.serialization.*

//-------- TOP LEVEL ----------------//


data class Interface(val name: String, val documentation: String, val props: List<Signature>)

//----------------------------------//
////-------- SIGNATURE ----------------//
interface Signature {
    val documentation: String
}

data class Property(
    val name: String,
    val type: KotPlotType,
    override val documentation: String
) : Signature

data class Method(
    val name: String,
    val returnType: KotPlotType,
    val parameters: List<Parameter>,
    override val documentation: String
) : Signature


//----------------------------------//
//-------- TYPE --------------------//
interface KotPlotType

data class LiteralType(
    val literal: String
) : KotPlotType

data class UnionType(
    val types: List<KotPlotType>
) : KotPlotType

data class ReferenceType(
    val name: String
) : KotPlotType

data class FunctionType(
    val parameters: List<Parameter>,
    val returnType: KotPlotType
) : KotPlotType

data class TupleType(
    val tupleTypes: List<KotPlotType>
) : KotPlotType

data class ArrayType(
    val elementType: KotPlotType
) : KotPlotType

data class TypeLiteral(
    val nestedTypes: List<KotPlotType>
) : KotPlotType

//--------------------------------//
//------- PARAMETER --------------//
data class Parameter(
    val name: String,
    val type: KotPlotType
)
//---------------------------------//

fun createGson() : Gson {
    val signatureTypeFactory = RuntimeTypeAdapterFactory
        .of(Signature::class.java, "signatureType")
        .registerSubtype(Property::class.java)
        .registerSubtype(Method::class.java)

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

