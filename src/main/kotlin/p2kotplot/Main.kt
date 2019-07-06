package p2kotplot

import p2kotplot.types.DeclarationFile
import java.io.File


//TODO: add "export type Datum = string | number | Date | null" to the plotlyToJSON
//TODO: add "export const Plots: StaticPlots;"  to the plotlyToJSON
//TODO convert the top 2 into kotlin
//TODO: fix Partial<> bug
//TODO: fix union type not working correctly
@ExperimentalStdlibApi
fun main() {
    val plotlyTypesFile = File("src/ts/data/plotlyTypes.json")
    val plotlyTypes = plotlyTypesFile.readText()
    val deserialized = plotlyTypes.parseTo<DeclarationFile>()


    JsonToKotPlot(deserialized)


}

