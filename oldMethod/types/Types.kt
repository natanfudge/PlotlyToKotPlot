package p2kotplot.types

import p2kotplot.addClass
import p2kotplot.addEnum
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import p2kotplot.JsonToKotPlot
import p2kotplot.toClassName

//-------- TOP LEVEL ----------------//
data class DeclarationFile(
    val typeAliases: List<TypeAlias>,
    val constants: List<Constant>,
    val interfaces: List<Interface>,
    val functions: List<FunctionSignature>
)

//------------------------------------//
//--------- TYPE ALIAS ---------------//
data class TypeAlias(
    val name: String,
    val type: KotPlotType
)

//-----------------------------------//
//---------- CONSTANT ---------------//
data class Constant(
    val name: String,
    val type: KotPlotType
)
//-------- TOP LEVEL ----------------//


data class Interface(val name: String, val documentation: String, val props: List<Signature>)

//----------------------------------//
////-------- SIGNATURE ----------------//
sealed class Signature(val name: String, val documentation: String)

class PropertySignature(
    name: String,
    val type: KotPlotType,
    documentation: String
) : Signature(name, documentation)

class FunctionSignature(
    name: String,
    val returnType: KotPlotType,
    val parameters: List<Parameter>,
    documentation: String
) : Signature(name, documentation)

//----------------------------------//
//-------- TYPE --------------------//
interface KotPlotType {
    /**
     * @param knownName Sometimes we already know the name, and we just want to create the type with that name.
     */
    fun getNameAndCreate(
        converter : JsonToKotPlot,
        knownName: String? = null
    ): TypeName
}







enum class closestOrxOryOrfalse {

}

//TODO: make this work for stuff like "x+y" etc
fun String.toTitleCase() = if (this.isEmpty()) this else this[0].toUpperCase() + this.substring(1)

fun String.toCamelCase() = if (this.isEmpty()) this else this[0].toLowerCase() + this.substring(1)



/**
 * E.g. Tuple<String,Int> becomes "TupleOfStringAndInt"
 */
fun TypeName.getRepresentativeName(): String = when {
    this is ClassName -> simpleName
    this is ParameterizedTypeName -> rawType.simpleName + "Of" + typeArguments.joinToString("And") {
        it.getRepresentativeName()
    }
    else -> TODO("Not sure how to handle the non-ClassName/ParameterizedTypeName case")
}




//--------------------------------//
//------- PARAMETER --------------//
data class Parameter(
    val name: String,
    val type: KotPlotType
)
//---------------------------------//

