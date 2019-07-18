package p2kotplot

import p2kotplot.ast.*
import p2kotplot.plotlytypes.*


class JsonToFBR(
    private val interfaceTypeData: List<Interface>,
    private val typeAliasData: List<TypeAlias>,
    private val functions: List<FunctionSignature>
) {
//    //TODO: handle type alias aswell
//    fun findType(name: String): Interface {
//        return interfaceTypeData.find { it.name == name }
//            ?: throw InvalidStateException("Could not find class with name $name")
//    }

//    fun getApi() = getApi()


//    init {
////        for (function in functions) {
////            function.emit()
////        }
//
//        val api =
//    }
    //TODO union functions are emitted later I think

    fun get(): FlatBuilderRepresentation {
//        val apiParameters = mutableListOf<ApiParameter>()
//        val builderFunctions = mutableListOf<BuilderFunction>()
//        val applyStatements = mutableListOf<String>()
//        val builderArrays = mutableListOf<String>()
//
//        val builderClass = BuilderClass(
//            name = this.builderName(),
//            builderFunctions = builderFunctions,
//            buildFunction = BuildFunction(
//                applyStatements
//            ),
//            arrays = builderArrays
//        )
//
//        val apiFunction = DslBuilderFunction(
//            name = this.name,
//            documentation = this.documentation,
//            builderClassName = this.builderName(),
//            parameters = apiParameters
//        )
//
//        return passTypeContext(data = this@JsonToAST, builderFunctionName = this.name) {
//            for (parameter in this@getApi.parameters) {
//                parameter.type.emit(
//                    createTypeContext(typeParameterName = parameter.name)
//                )
//            }
//        }


        return FlatBuilderRepresentation(
            builderClasses = mutableListOf(),
            builderFunctions = mutableListOf(),
            parameters = mutableListOf(),
            enums = mutableListOf()
        ).also {
            for (function in functions) {
                it.addBuilderFunction(
                    name = function.name, inClass = TopLevel,
                    /* type = BuilderFunctionsType.Root,*/
                    builderNameOfConstructedType = function.name.toBuilderName(),
                    // isOptional doesn't really mean anything in this case
                    isOptional = true
                )
                it.addBuilderClass(name = function.name.toBuilderName())
                for (parameter in function.parameters) {
                    parameter.type.add(
                        builder = it,
                        typeData = TypeData(interfaceTypeData, typeAliasData),
                        builderFunctionInClass = function.name.toBuilderName(),
                        paramInConstructorOfClass = function.name.toBuilderName(),
                        nameAsParameter = parameter.name,
                        isOptional = parameter.optional,
                        functionAppearsIn = function.name,
                        documentationAsParameter = "",
                        isPartial = false,
                        overloadNum = StartingOverloadNum
                    )
                }
            }

        }

//        return MutableBuilderTree(
//            data = TypeData(interfaceTypeData, typeAliasData),
//            builderName = this.name,
//            wrappedBuilder = createEmptyBuilder(this.name, BuilderType.Root)
//        ).also {
//            for (parameter in this.parameters) {
//                parameter.type.add(it,parameter.name)
//            }
//        } .extractFinishedBuilderAtTheEndOfProcessing()


//        return DslBuilderGroup(
//            name = this.name,
//            builderClass = builderClass,
//            dslBuilder = apiFunction
//        )
    }

}

/*
fun foo(primitive, reference, array, function, intersection, literal, tuple, typeLiteral, union) =>

fun foo(primitive, function, intersectionPart1,intersectionPart2,..., enumOfLiteral, tupleAsRange, unionPart1, init: FooBuilder.() -> Unit){
    val JsonObject = FooBuilder(primitive, function, intersectionPart1,intersectionPart2).apply(init).build()
    use(JsonObject)
}

fun foo(primitive, function, intersectionPart1,intersectionPart2,..., enumOfLiteral, tupleAsRange, unionPart2, init: FooBuilder.() -> Unit){
    val JsonObject = FooBuilder(primitive, function, intersectionPart1,intersectionPart2).apply(init).build()
    use(JsonObject)
}

fun foo(primitive, function, intersectionPart1,intersectionPart2,..., enumOfLiteral, tupleAsRange, unionPart..., init: FooBuilder.() -> Unit){
    val JsonObject = FooBuilder(primitive, function, intersectionPart1,intersectionPart2).apply(init).build()
    use(JsonObject)
}

class FooBuilder(primitive, function, intersectionPart1, intersectionPart2, enumOfLiteral, tupleAsRange, union : Any){
    val Map (mutable)
    val Array (mutable)

    fun reference(referenceProp1, referenceProp2, ...){
        Map.addProp(name : reference, value : JsonObject(referenceProp1,referenceProp2,...)
    }

    fun typeLiteral(typeLiteralProp1, typeLiteralProp2, ...){
         Map.addProp(typeLiteral : reference, value : JsonObject(typeLiteralProp1,typeLiteralProp2,...)
    }

    fun singularOfArray(arrayGenericType){
        Array.add(arrayGenericType)
    }


    internal fun build(){
//        Map.apply {
            //TODO: on a later stage, convert nulls to default values

            if(Array.isNotEmpty()) Map.add(Array)
            if(primitive != null) Map.add(primitive)
            if(function != null) throw UnsupportedOperationException("Don't know how to serialize functions)
            if(intersectionPart1 != null) Map.add(intersectionPart1)
            if(intersectionPart2 != null) Map.add(intersectionPart2)
            if(enumOfLiteral != null) Map.add(enumOfLiteral)
            if(tupleAsRange != null) Map.add(tupleAsRange)
            if(union != null){
                if(union is Number) Map.add(union)
                if(union is String) Map.add(union)
                ...
            }
        }

        return JsonObject(Map)
    }
}

 */