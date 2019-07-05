import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

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
    val types: KotPlotType
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
sealed class KotPlotType {
    abstract fun getName(builder: TypeSpec.Builder): TypeName
}

/**
 * E.g. "thing1"
 */
data class LiteralType(val literal: String) : KotPlotType() {

    //TODO fix upper/ lower casing
    //TODO serialize properly
    override fun getName(builder: TypeSpec.Builder): TypeName =
        getLiteralName().also { builder.addObject(it) }.toClassName()


    //TODO: should actually return an enum
}

fun LiteralType.getLiteralName(): String = literal.toTitleCase()


enum class closestOrxOryOrfalse {

}

//TODO: make this work for stuff like "x+y" etc
fun String.toTitleCase() = if (this.isEmpty()) this else this[0].toUpperCase() + this.substring(1)

/**
 * Create an enum type of all the literals and return its name
 */
fun Iterable<LiteralType>.getName(builder: TypeSpec.Builder): TypeName =
    this.joinToString("Or") { it.getLiteralName() }.also {
        builder.addEnum(it) {
            for (literalType in this@getName) {
                addEnumConstant(literalType.getLiteralName())
            }
        }
    }.toClassName()

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


data class UnionType(val types: List<KotPlotType>) : KotPlotType() {
    override fun getName(builder: TypeSpec.Builder): TypeName {

        val literals = types.filterIsInstance<LiteralType>()
        val references = types.filter { it !is LiteralType }


        //TODO: when we get some literals and some references , we convert the literals into objects and the references into classes,
        //TODO: and return a sealed class that is the objects or the classes.
        //TODO: but if we get only literals, we return an enum class.

        // If there are actual types in the union type the only way to handle it would be a sealed class where
        // each reference is a subclass of the sealed class and then all the literals are also a enum as a subclass.
        if (references.isNotEmpty()) {
            val typeNames = mutableListOf<String>()

            val sealedClassName = references.joinToString { reference ->
                reference.getName(builder).getRepresentativeName().also { typeNames.add(it) }
            }

            //TODO: think about adding the subclasses as an inner class of the sealed class

            // Add sealed class
            builder.addClass(className = sealedClassName) {
                addModifiers(KModifier.SEALED)
            }

            // Add subclasses of sealed class
            for (typeName in typeNames) {
                builder.addClass(className = typeName) {
                    superclass(sealedClassName.toClassName())
                }
            }

            return sealedClassName.toClassName()
        } else {
            return literals.getName(builder)
        }


        //TODO: handle duplication of union type
        //TODO: handle serialization of union/sealed types


    }
}

data class ReferenceType(val name: String) : KotPlotType() {
    override fun getName(builder: TypeSpec.Builder): TypeName =
        if (name.startsWith("Partial<")) {
            name.removePrefix("Partial<").removeSuffix(">").toClassName().copy(nullable = true)
        } else {
            name.toTitleCase().toClassName()
        }
}

data class FunctionType(val parameters: List<Parameter>, val returnType: KotPlotType) : KotPlotType() {
    override fun getName(builder: TypeSpec.Builder): TypeName =
        LambdaTypeName.get(
            receiver = null,
            parameters = this.parameters.map { builder.getParameterSpec(it) },
            returnType = returnType.getName(builder)
        )

}

//TODO: make this into a proper data class with "first", "second" etc as properties
data class TupleType(val tupleTypes: List<KotPlotType>) : KotPlotType() {
    override fun getName(builder: TypeSpec.Builder): TypeName {
        val typeNames = this.tupleTypes.map { it.getName(builder) }

        return "Tuple".toClassName().parameterizedBy(*typeNames.toTypedArray())
    }
}

data class ArrayType(val elementType: KotPlotType) : KotPlotType() {
    override fun getName(builder: TypeSpec.Builder): TypeName =
        "List".toClassName().parameterizedBy(elementType.getName(builder))
}

//TODO: change this to something that better describes type literals
var typeLiteralCount = 1


/**
 * E.g. { thing : "value", otherThing : {...} }
 */
data class TypeLiteral(val nestedProperties: List<PropertySignature>) : KotPlotType() {
    override fun getName(builder: TypeSpec.Builder): TypeName {
        val typeLiteralName = "TypeLiteralNum${typeLiteralCount++}"
        builder.addClass(className = typeLiteralName) {
            addSignatures(nestedProperties)
//            for (property in nestedProperties) {
//                addPropertySignature(property)
//            }
        }
        return typeLiteralName.toClassName()
    }
}

//--------------------------------//
//------- PARAMETER --------------//
data class Parameter(
    val name: String,
    val type: KotPlotType
)
//---------------------------------//

