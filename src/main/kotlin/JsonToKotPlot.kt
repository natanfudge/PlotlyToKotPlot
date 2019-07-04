import com.google.gson.Gson
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import com.google.gson.GsonBuilder
import java.io.File


@ExperimentalStdlibApi
fun main() {
    val plotlyTypesFile = File("src/ts/plotlyTypes.json")
    val plotlyTypes = plotlyTypesFile.readText()
    val deserialized = plotlyTypes.parseTo<Array<Interface>>()

//    val test = Interface(
//        name = "amar",
//        documentation = "",
//        props = listOf(
//            Method(
//                name = "destroy",
//                documentation = "",
//                parameters = listOf(),
//                returnType = ReferenceType("oomar")
//            )
//        )
//    )
//
//    val asString = test.stringify()
//    println(asString)
    val x = 2


}

