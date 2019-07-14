package p2kotplot.plotlytypes

import p2kotplot.ast.FlatBuilderRepresentation
//import p2kotplot.JsonToKotPlot
import p2kotplot.ast.TypeData

//-------- TOP LEVEL ----------------//
data class DeclarationFile(
    val typeAliases: List<TypeAlias>,
    val constants: List<Constant>,
    val interfaces: List<Interface>,
    val functions: List<FunctionSignature>
)

//--------- TYPE ALIAS ---------------//
data class TypeAlias(val name: String, val type: KotPlotType)

//---------- CONSTANT ---------------//
data class Constant(val name: String, val type: KotPlotType)

//-----------INTERFACE --------------//
data class Interface(val name: String, val documentation: String, val props: List<Signature>)

//----------------------------------//
////-------- SIGNATURE ----------------//
sealed class Signature(/*val name: String, val documentation: String*/)

data class PropertySignature(
    val name: String,
    val type: KotPlotType,
    val documentation: String,
    val optional: Boolean
) : Signature(/*name, documentation*/)

data class FunctionSignature(
    val name: String,
    val returnType: KotPlotType,
    val parameters: List<Parameter>,
    val documentation: String
) : Signature(/*name, documentation*/)

//data class TypeContext(val data: JsonToKotPlotAST,
//                       val apiFunctionName: String,
//                       val builderClassName : String
//                       )


//----------------------------------//
//-------- TYPE --------------------//
interface KotPlotType {

    fun add(
        builder: FlatBuilderRepresentation,
        typeData: TypeData,
        builderClassIn: String?,
        nameAsParameter: String,
        isOptional: Boolean,
        functionAppearsIn: String,
        documentationAsParameter: String,
        /**
         * In this case:
         * ```
         * interface Foo{
         *  x : Partial<Bar>
         * }
         * interface Bar{
         *  y : string
         *   ...
         *  }
         *  ```
         *  It is both used by `Foo` to tell `Bar` that It's partial, and used by `Bar` to tell y and other props that they are optional.
         */

        isPartial: Boolean = false
    ) {

    }

//    /**
//     * @param knownName Sometimes we already know the name, and we just want to create the type with that name.
//     */
//    fun getNameAndCreate(
//        converter: JsonToKotPlot,
//        knownName: String? = null
//    ): TypeName


//    fun getName
}


enum class closestOrxOryOrfalse {

}

//TODO: make this work for stuff like "x+y" etc
fun String.toTitleCase() = if (this.isEmpty()) this else this[0].toUpperCase() + this.substring(1)

fun String.toCamelCase() = if (this.isEmpty()) this else this[0].toLowerCase() + this.substring(1)


///**
// * E.g. Tuple<String,Int> becomes "TupleOfStringAndInt"
// */
//fun TypeName.getRepresentativeName(): String = when {
//    this is ClassName -> simpleName
//    this is ParameterizedTypeName -> rawType.simpleName + "Of" + typeArguments.joinToString("And") {
//        it.getRepresentativeName()
//    }
//    else -> TODO("Not sure how to handle the non-ClassName/ParameterizedTypeName case")
//}
//

fun List<PropertySignature>.sortedSoUnionsAreLast() = sortedWith(Comparator { sig1, sig2 ->
    when {
        sig1.type is UnionType && sig2.type is UnionType -> 0
        // sig1 comes before
        sig1.type is UnionType -> 1
        // sig2 comes before
        sig2.type is UnionType -> -1
        else -> 0
    }
})

fun List<PropertySignature>.addTypes(
    builder: FlatBuilderRepresentation,
    typeData: TypeData,
    builderClassIn: String,
    functionAppearsIn: String,
    isPartial: Boolean
) {
    // Unions are last so they can duplicate the other parameters
    for (prop in this.sortedSoUnionsAreLast()) {
        prop.type.add(
            builder = builder,
            typeData = typeData,
            builderClassIn = builderClassIn,
            nameAsParameter = prop.name,
            isOptional = isPartial || prop.optional,
            functionAppearsIn = functionAppearsIn,
            documentationAsParameter = prop.documentation
        )
    }


}

//--------------------------------//
//------- PARAMETER --------------//
data class Parameter(val name: String, val type: KotPlotType, val optional: Boolean)
//---------------------------------//

