package p2kotplot

import com.squareup.kotlinpoet.*
import kotlinx.serialization.Serializable
import p2kotplot.types.*
import java.io.File


const val PackageName = ""
fun String.toClassName() = ClassName(packageName = PackageName, simpleName = this)

@Suppress("MemberVisibilityCanBePrivate")
class JsonToKotPlot(declarationFile: DeclarationFile) {
     val fileBuilder: FileSpec.Builder = FileSpec.builder(PackageName, "PlotlyTypes")

    init {
        val file = fileBuilder.apply {
            val some = declarationFile.interfaces.subList(0, 50)
            addInterfaces(some)
            addTypeAliases(declarationFile.typeAliases)
            addConstants(declarationFile.constants)
            addFunctions(declarationFile.functions)
        }.build()


        file.writeTo(File("src\\main\\kotlin"))

    }


    fun addTypeAliases(typeAliases: List<TypeAlias>) {
        for (typeAlias in typeAliases) {
            typeAlias.type.getNameAndCreate(knownName = typeAlias.name, converter = this)
        }
    }

    fun addConstants(constants: List<Constant>) {
        //TODO
    }

    fun addFunctions(functions: List<FunctionSignature>) {
        //TODO
    }

    fun FileSpec.Builder.addInterfaces(interfaces: List<Interface>) {
        for (interfaceDec in interfaces) {
            addDataClass(
                className = interfaceDec.name,
                signatures = interfaceDec.props,
                documentation = interfaceDec.documentation
            )
        }
    }

//TODO: use only the filespec one, type should not use the typespec to add new type most of the time. (it causes it to make inner classes)

    fun addDataClass(className: String, signatures: List<Signature>, documentation: String) {
        addClass(className = className) {
            if (signatures.filterIsInstance<PropertySignature>().isNotEmpty()) {
                addModifiers(KModifier.DATA)
                addSignatures(signatures,typeBuilder = this)
            }
            addAnnotation(Serializable::class)
            addKdoc(documentation)
        }
    }


    fun addSignatures(signatures: List<Signature>, typeBuilder:TypeSpec.Builder) {
        typeBuilder.primaryConstructor {
            for (signature in signatures) {
                when (signature) {
                    is FunctionSignature -> typeBuilder.addMethodSignature(signature)
                    is PropertySignature -> typeBuilder.addPropertySignature(signature, funspecBuilder = this)
                }
            }

        }

    }

    fun List<Signature>.getKDoc(): String {
        return this.joinToString("\n") {
            "[${it.name}]: ${it.documentation}"
        }
    }

    fun TypeSpec.Builder.addMethodSignature(signature: FunctionSignature) {
        //TODO: think how to add methods
    }


    fun TypeSpec.Builder.addParameter(parameter: Parameter) {

    }




    fun getParameterSpec(parameter: Parameter): ParameterSpec = parameter(
        name = parameter.name,
        type = parameter.type.getNameAndCreate(converter = this)
    )


    fun TypeSpec.Builder.addPropertySignature(
        signature: PropertySignature,
        funspecBuilder: FunSpec.Builder
    ) {

        //TODO: change from adding properties to adding primary constructor properties
        funspecBuilder.addParameter(
            name = signature.name, type = signature.type.getNameAndCreate(
                converter = this@JsonToKotPlot
            )
        )
        addProperty(
            name = signature.name, type = signature.type.getNameAndCreate(
                converter = this@JsonToKotPlot
            )
        ) {
            initializer(signature.name)
            addKdoc(signature.documentation)
        }
    }



}