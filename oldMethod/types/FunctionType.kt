package p2kotplot.types

import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.TypeName
import kotlinx.serialization.json.*
import p2kotplot.JsonToKotPlot

data class FunctionType(val parameters: List<Parameter>, val returnType: KotPlotType) : KotPlotType {
    override fun getNameAndCreate(
        converter: JsonToKotPlot,
        knownName: String?
    ): TypeName =
        LambdaTypeName.get(
            receiver = null,
            parameters = this.parameters.map { converter.getParameterSpec(it) },
            returnType = returnType.getNameAndCreate(converter)
        )

}


fun main() {
    val x = JsonObject(mapOf("amar" to JsonLiteral(3), "oomer" to JsonLiteral(4)))
    print(x)
}

//val x : JsonArray

fun plotData() {
//JsonBuilder
}

class NewPlotBuilder {
//    fun root()
}

//fun newPlot(root: Root, data: Data[], layout?: Partial<Layout>, config?: Partial<Config>): Promise<PlotlyHTMLElement>;
//fun newPlot(init: NewPlotBuilder.() -> Unit){
//    fun newPlot(root: Root, data: Data[], layout?: Partial<Layout>, config?: Partial<Config>): Promise<PlotlyHTMLElement>
//}

/*
fun foo(primitive, reference, array, function, intersection, literal, tuple, typeLiteral, union) =>

class FooBuilder(primitive, function){
    val Map (mutable)
    val Array (mutable)

    fun reference(referenceProp1, referenceProp2, ...){
        Map.addProp(name : reference, value : JsonObject(referenceProp1,referenceProp2,...)
    }
    fun singularOfArray(arrayGenericType){
        Array.add(arrayGenericType)
    }

    fun


    internal fun build(){
        Map.apply {
            if(Array.isNotEmpty()) add(Array)
            if(primitive != null) add(primitive)
            if(function != null) throw UnsupportedOperationException("Don't know how to serialize functions)
        }

        return JsonObject(Map)
    }
}

 */