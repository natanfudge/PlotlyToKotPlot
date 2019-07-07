package p2kotplot.plotlytypes

import p2kotplot.*
import sun.plugin.dom.exception.InvalidStateException

private fun String.builderName() = this.toTitleCase() + "Builder"

data class ReferenceType(val name: String) : KotPlotType {
    override fun emit(context: TypeContext) =
        when (this.name) {
            "number", "string" -> emitValueType(context, this.name.toTitleCase())
            else -> emitReferenceType(context)
        }


    private fun emitValueType(context: TypeContext, kotlinName: String) {
        context.apiFunction.parameters.add(
            ApiParameter(name = context.typeParameterName, type = kotlinName)
        )

        context.builderClass.buildFunction.applyStatements
            .add("if($kotlinName != null) jsonMap[\"$kotlinName\"] = JsonLiteral($kotlinName)")
    }

    private fun emitReferenceType(context: TypeContext) {
        val params = mutableListOf<ApiParameter>()
        context.builderClass.builderFunctions.add(
            BuilderFunction(
                name = context.typeParameterName,
                type = BuilderFunctionType.Object,
                params = params
            )
        )


        val typeDeclaration = context.data.findType(this.name)
//        val apiParameters = mutableListOf<ApiParameter>()
        val builderFunctions = mutableListOf<BuilderFunction>()
        val applyStatements = mutableListOf<String>()
        val builderArrays = mutableListOf<String>()

//        val builderClass =

        for (prop in typeDeclaration.props) {
            if (prop !is PropertySignature) throw InvalidStateException("Did not expect to find an interface with methods")
            prop.type.emit(
                context.copy(
                    apiFunction = ApiFunction(
                        name = this.name,
                        documentation = typeDeclaration.documentation,
                        parameters = params,
                        builderClassName = context.typeParameterName.builderName()
                    ),
                    builderClass = BuilderClass(
                        name = context.typeParameterName.builderName(),
                        builderFunctions = builderFunctions,
                        buildFunction = BuildFunction(
                            applyStatements
                        ),
                        arrays = builderArrays
                    ),
                    typeParameterName = prop.name
                )
            )
        }
    }


//    override fun emit(
//        apiFunction: ApiFunction,
//        builderConstructor: BuilderConstructor,
//        builder: TypeSpec.Builder,
//        buildFunction: FunSpec.Builder
//    ) {
////        val referenceTypeName = if (name.startsWith("Partial<")) {
////            name.removePrefix("Partial<").removeSuffix(">").toClassName().copy(nullable = true)
////        } else {
////            name.toTitleCase().toClassName()
////        }
//
//        val referenceTypeName = if (name.startsWith("Partial<")) {
//            name.removePrefix("Partial<").removeSuffix(">")
//        } else name
//
//
//        when(referenceTypeName){
//            "number", "string" -> emitValueType(apiFunction, builderConstructor, builder, buildFunction)
//            else -> emitReferenceType(apiFunction, builderConstructor, builder, buildFunction)
//        }
//    }
//
//    fun emitValueType(apiFunction: ApiFunction,
//        builderConstructor: BuilderConstructor,
//        builder: TypeSpec.Builder,
//        buildFunction: FunSpec.Builder){
//    }
//
//    fun emitReferenceType(apiFunction: ApiFunction,
//        builderConstructor: BuilderConstructor,
//        builder: TypeSpec.Builder,
//        buildFunction: FunSpec.Builder){
//    }

//    override fun getNameAndCreate(
//        converter: JsonToKotPlot,
//        knownName: String?
//    ): TypeName =
//        if (name.startsWith("Partial<")) {
//            name.removePrefix("Partial<").removeSuffix(">").toClassName().copy(nullable = true)
//        } else {
//            name.toTitleCase().toClassName()
//        }
}