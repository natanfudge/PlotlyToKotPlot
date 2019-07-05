import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javafx.beans.property.Property
import kotlin.reflect.KClass

//-------- TOP LEVEL ----------------//


data class Interface(val name: String, val documentation: String, val props: List<Signature>)

//----------------------------------//
////-------- SIGNATURE ----------------//
sealed class Signature

data class PropertySignature(
    val name: String,
    val type: KotPlotType,
    val documentation: String
) : Signature()

data class MethodSignature(
    val name: String,
    val returnType: KotPlotType,
    val parameters: List<Parameter>,
    val documentation: String
) : Signature()


//----------------------------------//
//-------- TYPE --------------------//
sealed class KotPlotType {
    abstract fun getName(builder: TypeSpec.Builder): TypeName
}

/**
 * E.g. "thing1"
 */
data class LiteralType(val literal: String) : KotPlotType() {
    override fun getName(builder: TypeSpec.Builder): TypeName = this.literal.toClassName()
    //TODO: should actually return an enum
}

data class UnionType(val types: List<KotPlotType>) : KotPlotType() {
    override fun getName(builder: TypeSpec.Builder): TypeName {
        //TODO: when we get some literals and some references , we convert the literal part into an enum,
        //TODO: and return a sealed class that is the reference type, or the enum.
        //TODO: but if we get only literals, we return the enum class purely.
        val typeNames = mutableListOf<String>()

        val sealedClassName = this.types.joinToString { type ->
            val typeName = type.getName(builder)
            if (typeName is ClassName) {
                typeName.simpleName.also { typeNames.add(it) }
            } else {
                TODO("Not sure how to handle the non-ClassName case")
            }

        }

        // Add sealed class
        builder.addType(builder.classType(className = sealedClassName) {
            addModifiers(KModifier.SEALED)
        })

        // Add subclasses of sealed class
        for (typeName in typeNames) {
            builder.addType(builder.classType(className = typeName) {
                superclass(sealedClassName.toClassName())
            })
        }

        //TODO: handle duplication of union type
        //TODO: handle serialization of union/sealed types


        return sealedClassName.toClassName()
    }
}

data class ReferenceType(val name: String) : KotPlotType() {
    override fun getName(builder: TypeSpec.Builder): TypeName = this.name.toClassName()
}

data class FunctionType(val parameters: List<Parameter>, val returnType: KotPlotType) : KotPlotType() {
    override fun getName(builder: TypeSpec.Builder): TypeName =
        LambdaTypeName.get(
            receiver = null,
            parameters = this.parameters.map { builder.getParameterSpec(it) },
            returnType = returnType.getName(builder)
        )

}

data class TupleType(val tupleTypes: List<KotPlotType>) : KotPlotType() {
    override fun getName(builder: TypeSpec.Builder): TypeName {
        val typeNames = this.tupleTypes.map { it.getName(builder) }

        return "List".toClassName().parameterizedBy(*typeNames.toTypedArray())
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
        val typeLiteralName = "TypeLiteral#${typeLiteralCount++}"
        builder.addType(builder.classType(className = typeLiteralName) {
            for (property in nestedProperties) {
                addPropertySignature(property)
            }
        })
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

