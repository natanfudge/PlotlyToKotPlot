const val tsNodeLocation = "C:\\Users\\natan\\AppData\\Roaming\\npm\\ts-node.cmd"
fun convertToJson(fixtureName : String){
    "$tsNodeLocation src/ts/plotly2json.ts src/test/kotlin/fixtures/$fixtureName.d.ts src/test/kotlin/out/$fixtureName.json".runCommand()
}