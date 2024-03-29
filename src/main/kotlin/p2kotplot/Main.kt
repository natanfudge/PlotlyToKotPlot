package p2kotplot

import kotlinx.serialization.Serializable
import kotlinx.serialization.compiledSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import p2kotplot.plotlytypes.DeclarationFile
import java.io.File
import kotlin.reflect.typeOf

//
//@ExperimentalStdlibApi
//fun main() {
////    val plotlyTypesFile = File("src/ts/data/plotlyTypes.json")
////    val plotlyTypes = plotlyTypesFile.readText()
////    val deserialized = plotlyTypes.parseTo<DeclarationFile>()
//
//
////    val api = JsonToFBR(
////        interfaceTypeData = deserialized.interfaces,
////        typeAliasData = deserialized.typeAliases,
////        functions = deserialized.functions
////    ).get()
//
//
////    FBRToKotPlot(api.extractDataAtTheEndOfProcessing())
//
//
//
//}

fun DeclarationFile.toKotlinApi() : KotlinApi{
    val fbr = JsonToFBR(
        interfaceTypeData = interfaces,
        typeAliasData = typeAliases,
        functions = functions
    ).get()

    return BuilderAssembly(fbr.extractDataAtTheEndOfProcessing()).assembleAll()
}

fun KotlinApi.writeTo(location: String) {

    KotlinWriter(this, targetFileName = File(location).nameWithoutExtension).writeTo(
        File(location).parentFile
    )
}

