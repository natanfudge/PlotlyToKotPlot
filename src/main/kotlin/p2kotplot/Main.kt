package p2kotplot

import p2kotplot.plotlytypes.DeclarationFile
import p2kotplot.util.parseTo
import java.io.File


@ExperimentalStdlibApi
fun main() {
    val plotlyTypesFile = File("src/ts/data/plotlyTypes.json")
    val plotlyTypes = plotlyTypesFile.readText()
    val deserialized = plotlyTypes.parseTo<DeclarationFile>()


    val api = JsonToFBR(
        interfaceTypeData = deserialized.interfaces,
        typeAliasData = deserialized.typeAliases,
        functions = deserialized.functions
    ).getApi()

//    print(api)

    AstToKotPlot(api.extractDataAtTheEndOfProcessing())

//    print(api)
    val x = 2


}

