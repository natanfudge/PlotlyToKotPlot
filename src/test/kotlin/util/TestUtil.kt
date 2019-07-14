package util

import p2kotplot.plotlytypes.DeclarationFile
import p2kotplot.plotlytypes.FunctionSignature
import p2kotplot.plotlytypes.Interface
import p2kotplot.plotlytypes.Parameter
import p2kotplot.util.gson
import p2kotplot.writeTo
import sun.plugin.dom.exception.InvalidStateException
import java.io.File
import java.lang.Exception
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

inline fun File.doesNotExist() = !exists()

fun fixture(name: String, category : String, tests: FixtureContext.() -> Unit) {
    FixtureContext(name, category).apply(tests)
}
const val tsNodeLocation = "C:\\Users\\natan\\AppData\\Roaming\\npm\\ts-node.cmd"
const val plotly2JsonLocation = "src/ts/plotly2json.ts"
class FixtureContext(private val fixtureName: String, private val fixtureCategory : String) {
    val fixtureDeclarationFile : DeclarationFile
    val generatedKotlinFile : String

    init {
        // Generate and parse declaration file
        val declarationFileLocation = generateDeclarationFile()
        fixtureDeclarationFile =  gson.fromJson(File(declarationFileLocation).readText(), DeclarationFile::class.java)

        val kotlinFileLocation = generateKotlinFile()
        generatedKotlinFile = File(kotlinFileLocation).readText()

    }

    private fun generateKotlinFile(): String {
        val kotlinFileLocation = "src/test/out/$fixtureName.kt"
        fixtureDeclarationFile.writeTo(kotlinFileLocation)
        return kotlinFileLocation
    }

//    fun fixtureDecFile(): DeclarationFile {
//        val targetLocation = generateDeclarationFile()
//        return gson.fromJson(File(targetLocation).readText(), DeclarationFile::class.java)
//    }

    private fun generateDeclarationFile(): String {
        val targetFolder = "src/test/out/$fixtureCategory"
        if (!File(targetFolder).exists()) File(targetFolder).mkdir()
        val declarationFileJsonLocation = "$targetFolder/$fixtureName.json"
        val fixtureLocation = "src/test/fixtures/$fixtureCategory/$fixtureName.d.ts"

        if(File(fixtureLocation).doesNotExist()) throw TestException("The fixture $fixtureLocation does not exist!")

        "$tsNodeLocation $plotly2JsonLocation $fixtureLocation $declarationFileJsonLocation".runCommand()
        return declarationFileJsonLocation
    }
//
//    fun generatedKotlinFile(): String {
//
////        while (!File(targetLocation).exists()) {
////        }
//        return File(targetLocation).readText()
//    }

    fun expectedDeclarationFile(init: DeclarationFileBuilder.() -> Unit){
//        val actualDeclarationFile = fixtureDecFile()
        val expectedDeclarationFile = declarationFile(init)
        expectedDeclarationFile isEqualTo  fixtureDeclarationFile
    }


}

fun String.runCommand(workingDir: File = File(System.getProperty("user.dir"))) {
    ProcessBuilder(*split(" ").toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor(60, TimeUnit.MINUTES)
}




class TestException(problem : String) : Exception(problem)