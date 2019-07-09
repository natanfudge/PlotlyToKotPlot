package p2kotplot

import p2kotplot.plotlytypes.DeclarationFile
import java.io.File


@ExperimentalStdlibApi
fun main() {
    val plotlyTypesFile = File("src/ts/data/plotlyTypes.json")
    val plotlyTypes = plotlyTypesFile.readText()
    val deserialized = plotlyTypes.parseTo<DeclarationFile>()


    val api = JsonToAST(
        interfaceTypeData = deserialized.interfaces,
        typeAliasData = deserialized.typeAliases,
        functions = deserialized.functions
    ).getApi()

//    print(api)

    AstToKotPlot(api)

//    print(api)
    val x = 2


}

