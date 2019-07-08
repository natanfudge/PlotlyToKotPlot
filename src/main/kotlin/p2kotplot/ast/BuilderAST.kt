package p2kotplot.ast

/**
 * A builder function with a corresponding builder class
 */
data class Builder(val builderFunctions: MutableList<BuilderFunction>, val builderClass: BuilderClass, val builderType: BuilderType)
data class BuilderFunction(val functionName: String, val parameters: MutableList<BuilderParameter>)
data class BuilderParameter(val name: String, val type: String)
data class BuilderClass(
    val name: String,
    val constructorParameters: MutableList<BuilderParameter>,
    val jsonArrayFields: MutableList<ArrayField>,
    val builders : MutableList<Builder>,
    val buildStatements : MutableList<BuildStatement>
)

data class BuildStatement(val variableName : String, val builderType: BuildStatementType)

enum class BuildStatementType{
//    Reference,
    Array,
    Primitive,
    Union
}

data class ArrayField(val arrayName: String, val arrayType: String)

enum class BuilderType{
    Reference,
    Array,
    Root
}
//
//
///**
// * ```
// * fun 'thing' <---- name (param1, param2, ...)
// *
// * class ThingBuilder {
// *      fun thing1(...) { ... }
// *      fun thing2(...) { ... }
// *      ...
// *      fun build(){ ... }
// * }
// * ```
// */
//data class DslBuilderGroup(
//    val name: String,
//    //TODO: make list of api functions, and add api around this to make it easier to use
//    val dslBuilder: DslBuilderFunction, val builderClass: BuilderClass/*, val type: DslBuilderType*/
//)
//
///**
// * ```
// * class Baz [<------ name] {
// *      fun thing1(...) { ... }
// *      fun thing2(...) { ... }
// *      ...
// *      fun build(){ ... }
// * }
// * ```
// */
////data class BuilderClass(
////    val name: String,
////    val arrays: MutableList<String>,
//////    val primaryConstructor: MutableList<ApiParameter>,
////    //TODO: add back primary constructor, and add api to make it easier to use
////    val builderFunctions: MutableList<DslBuilderGroup>,
////    val buildFunction: BuildFunction
////)
//
///**
// * ```
// * class ThingBuilder {
// *      ''' <--------------  BuilderFunction start
// *      fun bar [<------- name] (param1, param2, ...){
// *          map.add("bar", param1, param2,...)
// *      }
// *      ''' <--------------  BuilderFunction end
// * }
// * ```
// */
////data class BuilderFunction(val name: String, val type: DslBuilderType, val params: MutableList<ApiParameter>)
//
//enum class DslBuilderType {
//    Object,
//    Array
//}
//
///**
// * ```
// * class ThingBuilder {
// *      ...
// *
// *      ''' <--------------  BuildFunction start
// *      fun build(){
// *          map.apply {
// *              statement1()
// *              statement2()
// *              statement3()
// *              ...
// *          }
// *          ...
// *          return JsonObject(...)
// *      }
// *      ''' <-------------- BuildFunction end
// * }
// * ```
// */
//data class BuildFunction(val applyStatements: MutableList<String>)
//
///**
// * ```
// * /**
// *  documentation
// * */
// * fun foo [<---- name] (param1, param2, ...){
// *      val jsonObject = FooBuilder [<--- builderClassName] (param1,param2,...).apply(init).build()
// * }
// * ```
// */
//data class DslBuilderFunction(
//    val name: String,
//    val builderClassName: String,
//    val parameters: MutableList<ApiParameter>,
//    val documentation: String
//)
//
///**
// * fun foo(     'x : Int' <---- ApiParameter     )
// */
//data class ApiParameter(val name: String, val type: String/*, val documentation: String? = null*/)
//
//fun passTypeContext(
//    data: JsonToAST,
//    builderFunctionName: String,
//    emitCalls: TypeContext.PassingOnContext.() -> Unit
//): DslBuilderGroup {
//
//
//    return TypeContext.PassingOnContext(data, builderFunctionName).apply(emitCalls).getDslBuilderGroup()
//}
//
//data class TypeContext(
//    /*private*/ val data: JsonToAST,
//    /*private*/ val dslBuilderFunction: DslBuilderFunction,
//    /* private*/ val builderClass: BuilderClass,
//                val typeParameterName: String
//) {
//
//    class PassingOnContext internal constructor(val data: JsonToAST, val builderFunctionName: String) {
//        private val apiParameters = mutableListOf<ApiParameter>()
//        private val builderFunctions = mutableListOf<DslBuilderGroup>()
//        private val applyStatements = mutableListOf<String>()
//        private val builderArrays = mutableListOf<String>()
//
////        fun x(){
////
////        }
//
//
//        private val builderClass = BuilderClass(
//            name = builderFunctionName.builderName(),
//            builderFunctions = builderFunctions,
//            buildFunction = BuildFunction(
//                applyStatements
//            ),
//            arrays = builderArrays
//        )
//
//        private val apiFunction = DslBuilderFunction(
//            name = builderFunctionName,
//            documentation = "",
//            builderClassName = builderFunctionName.builderName(),
//            parameters = apiParameters
//        )
//
//        fun createTypeContext(typeParameterName: String): TypeContext {
//            return TypeContext(
//                data = data,
//                dslBuilderFunction = DslBuilderFunction(
//                    name = builderFunctionName,
//                    documentation = "",
//                    builderClassName = builderFunctionName.builderName(),
//                    parameters = apiParameters
//                ),
//                builderClass = builderClass,
//                typeParameterName = typeParameterName
//            )
//        }
//
//        fun getDslBuilderGroup() = DslBuilderGroup(
//            name = builderFunctionName,
//            builderClass = builderClass,
//            dslBuilder = apiFunction
//        )
//
//    }
//
//    //    constructor(builderFunctionName : String, buil)
//    fun passOn(builderFunctionName: String, emitCalls: PassingOnContext.() -> Unit) {
//        val builderGroup = passTypeContext(data, builderFunctionName, emitCalls)
//        this.builderClass.builderFunctions.add()
//    }
//
//
//    fun findType(name: String): Interface = data.findType(name)
//    fun addValueParameter(apiParameter: ApiParameter) = dslBuilderFunction.parameters.add(apiParameter)
//    fun addBuildFunctionStatement(statement: String) = builderClass.buildFunction.applyStatements.add(statement)
//    fun addBuilderFunction(builderFunction: BuilderFunction) = builderClass.builderFunctions.add(builderFunction)
//}
//
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