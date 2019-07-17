package util

import Builder
import p2kotplot.plotlytypes.*

@DslMarker
annotation class TestBuilder

fun reference(name: String) = ReferenceType(name)
fun union(vararg referenceTypes : String) = UnionType(referenceTypes.map { reference(it) })
fun union(vararg types : KotPlotType) = UnionType(types.toList())
fun array(elementType:String) = ArrayType(ReferenceType(elementType))

fun declarationFile(init: DeclarationFileBuilder.() -> Unit): DeclarationFile {
    return DeclarationFileBuilder().apply(init).build()
}

//-------- TOP LEVEL ----------------//
@TestBuilder
class DeclarationFileBuilder {
    private val typeAliases: MutableList<TypeAlias> = mutableListOf()
    private val constants: MutableList<Constant> = mutableListOf()
    private val interfaces: MutableList<Interface> = mutableListOf()
    private val functions: MutableList<FunctionSignature> = mutableListOf()

    fun typeAlias(name: String, type: KotPlotType) {
        typeAliases.add(TypeAlias(name, type))
    }

    fun constant(name: String, type: KotPlotType) {
        constants.add(Constant(name, type))
    }

    fun anInterface(name: String, documentation: String = "", props: SignatureListBuilder.() -> Unit) {
        interfaces.add(Interface(name, documentation, SignatureListBuilder().apply(props).build()))
    }

    fun function(
        name: String,
        returnType: KotPlotType = ReferenceType(""),
        documentation: String = "",
        parameters: ParameterListBuilder.() -> Unit
    ) {
        functions.add(
            FunctionSignature(
                name,
                returnType,
                ParameterListBuilder().apply(parameters).build(),
                documentation
            )
        )
    }

    fun build() = DeclarationFile(typeAliases, constants, interfaces, functions)
}

@Builder
class SignatureListBuilder {
    private val signatures: MutableList<Signature> = mutableListOf()
    fun property(name: String, type: KotPlotType, documentation: String = "", optional: Boolean = false) {
        signatures.add(PropertySignature(name, type, documentation, optional))
    }

    fun property(name: String, type: String, documentation: String = "", optional: Boolean = false) {
        signatures.add(PropertySignature(name, ReferenceType(type), documentation, optional))
    }

    fun build() = signatures
}

@Builder
class ParameterListBuilder {
    private val parameters: MutableList<Parameter> = mutableListOf()
    fun parameter(name: String, type: KotPlotType, optional: Boolean = false) {
        parameters.add(Parameter(name, type, optional))
    }

    fun parameter(name: String, type: String, optional: Boolean = false) {
        parameters.add(Parameter(name, ReferenceType(type), optional))
    }


    fun build() = parameters
}

