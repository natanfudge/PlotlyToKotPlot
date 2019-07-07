package p2kotplot.plotlytypes

import p2kotplot.*
import sun.plugin.dom.exception.InvalidStateException

fun String.builderName() = this.toTitleCase() + "Builder"

data class ReferenceType(val name: String) : KotPlotType {
    override fun emit(context: TypeContext) =
        when (this.name) {
            "number", "string" -> emitValueType(context)
            else -> emitReferenceType(context)
        }


    private fun emitValueType(context: TypeContext) {
        context.addValueParameter(
            ApiParameter(name = context.typeParameterName, type = this.name.toTitleCase())
        )

        val paramName = context.typeParameterName

        context.addBuildFunctionStatement("if($paramName != null) jsonMap[\"$paramName\"] = JsonLiteral($paramName)")
    }

    private fun emitReferenceType(context: TypeContext) {
        val params = mutableListOf<ApiParameter>()
        context.addBuilderFunction(
            BuilderFunction(
                name = context.typeParameterName,
                type = DslBuilderType.Object,
                params = params
            )
        )


        val typeDeclaration = context.findType(this.name)

        context.passOn(builderFunctionName = this.name) {
            for (prop in typeDeclaration.props) {
                if (prop !is PropertySignature) throw InvalidStateException("Did not expect to find an interface with methods")
                prop.type.emit(
                    createTypeContext(
                        typeParameterName = context.typeParameterName
                    )
                )
            }
        }
//        for (prop in typeDeclaration.props) {
//            if (prop !is PropertySignature) throw InvalidStateException("Did not expect to find an interface with methods")
//            prop.type.emit(
//                context.copy(
//                    dslBuilderFunction = DslBuilder(
//                        name = this.name,
//                        documentation = typeDeclaration.documentation,
//                        parameters = params,
//                        builderClassName = context.typeParameterName.builderName()
//                    ),
//                    builderClass = BuilderClass(
//                        name = context.typeParameterName.builderName(),
//                        builderFunctions = builderFunctions,
//                        buildFunction = BuildFunction(
//                            applyStatements
//                        ),
//                        arrays = builderArrays
//                    ),
//                    typeParameterName = prop.name
//                )
//            )
//        }

//        context.builderClass.buildFunction.applyStatements.add()
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