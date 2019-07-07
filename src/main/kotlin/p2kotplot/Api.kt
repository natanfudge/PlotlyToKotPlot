package p2kotplot

/**
 * ```
 * fun 'thing' <---- name (param1, param2, ...)
 *
 * class ThingBuilder {
 *      fun thing1(...) { ... }
 *      fun thing2(...) { ... }
 *      ...
 *      fun build(){ ... }
 * }
 * ```
 */
data class Api(val name: String, val apiFunction: ApiFunction, val builderClass: BuilderClass)

/**
 * ```
 * class Baz [<------ name] {
 *      fun thing1(...) { ... }
 *      fun thing2(...) { ... }
 *      ...
 *      fun build(){ ... }
 * }
 * ```
 */
data class BuilderClass(
    val name: String,
    val arrays : MutableList<String>,
//    val primaryConstructor: MutableList<ApiParameter>,
    val builderFunctions: MutableList<BuilderFunction>,
    val buildFunction: BuildFunction
)

/**
 * ```
 * class ThingBuilder {
 *      ''' <--------------  BuilderFunction start
 *      fun bar [<------- name] (param1, param2, ...){
 *          map.add("bar", param1, param2,...)
 *      }
 *      ''' <--------------  BuilderFunction end
 * }
 * ```
 */
data class BuilderFunction(val name: String, val type: BuilderFunctionType, val params: MutableList<ApiParameter>)

enum class BuilderFunctionType{
    Object,
    Array
}

/**
 * ```
 * class ThingBuilder {
 *      ...
 *
 *      ''' <--------------  BuildFunction start
 *      fun build(){
 *          map.apply {
 *              statement1()
 *              statement2()
 *              statement3()
 *              ...
 *          }
 *          ...
 *          return JsonObject(...)
 *      }
 *      ''' <-------------- BuildFunction end
 * }
 * ```
 */
data class BuildFunction(val applyStatements: MutableList<String>)

/**
 * ```
 * /**
 *  documentation
 * */
 * fun foo [<---- name] (param1, param2, ...){
 *      val jsonObject = FooBuilder [<--- builderClassName] (param1,param2,...).apply(init).build()
 * }
 * ```
 */
data class ApiFunction(
    val name: String,
    val builderClassName: String,
    val parameters: MutableList<ApiParameter>,
    val documentation: String
)

/**
 * fun foo(     'x : Int' <---- ApiParameter     )
 */
data class ApiParameter(val name: String, val type: String/*, val documentation: String? = null*/)

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