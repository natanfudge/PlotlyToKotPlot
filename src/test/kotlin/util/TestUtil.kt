package util

import p2kotplot.KotlinApi
import p2kotplot.plotlytypes.DeclarationFile
import p2kotplot.toKotlinApi
import p2kotplot.util.GsonTest
import p2kotplot.writeTo
import util.kotlinApiBuilders.KotlinApiBuilder
import util.kotlinApiBuilders.kotlinApi
import java.io.File
import java.lang.Exception
import java.util.concurrent.TimeUnit

@Suppress("NOTHING_TO_INLINE")
inline fun File.doesNotExist() = !exists()

inline fun fixture(name: String, category: String, tests: FixtureContext.() -> Unit) {
    FixtureContext(name, category).apply(tests)
}

const val tsNodeLocation = "C:\\Users\\natan\\AppData\\Roaming\\npm\\ts-node.cmd"
const val plotly2JsonLocation = "src/ts/plotly2json.ts"

const val updateFiles = false

class FixtureContext(fixtureName: String, private val fixtureCategory: String) {
    private val targetLocation = "$fixtureCategory/$fixtureName"

    val fixtureDeclarationFile: DeclarationFile
//    val generatedKotlinFile : String

    init {
        // Generate and parse declaration file
        val declarationFileLocation = generateDeclarationFile()
        fixtureDeclarationFile = GsonTest.gson.fromJson(File(declarationFileLocation).readText(), DeclarationFile::class.java)

//        val kotlinFileLocation = generateKotlinFile()
//        generatedKotlinFile = File(kotlinFileLocation).readText()

    }

     fun fixtureAsKotlinApi(): KotlinApi = fixtureDeclarationFile.toKotlinApi()

     fun writeToFile(kotlinApi: KotlinApi): String {
        val kotlinFileLocation = "src/test/out/$targetLocation.kt"
         kotlinApi.writeTo(kotlinFileLocation)
        return kotlinFileLocation
    }

//    fun fixtureDecFile(): DeclarationFile {
//        val targetLocation = generateDeclarationFile()
//        return gson.fromJson(File(targetLocation).readText(), DeclarationFile::class.java)
//    }

    private fun generateDeclarationFile(): String {
        val targetFolder = "src/test/out/$fixtureCategory"
        if (!File(targetFolder).exists()) File(targetFolder).mkdir()
        val declarationFileJsonLocation = "src/test/out/$targetLocation.json"
        val fixtureLocation = "src/test/fixtures/$targetLocation.d.ts"

        if (File(fixtureLocation).doesNotExist()) throw TestException("The fixture $fixtureLocation does not exist!")

        if(updateFiles) "$tsNodeLocation $plotly2JsonLocation $fixtureLocation $declarationFileJsonLocation".runCommand()
        return declarationFileJsonLocation
    }

    inline fun expectedDeclarationFile(init: DeclarationFileBuilder.() -> Unit) {
        val expectedDeclarationFile = declarationFile(init)
        expectedDeclarationFile assertEqualsTo fixtureDeclarationFile
    }

    inline fun expectedKotlinApi(init: KotlinApiBuilder.() -> Unit) {
        val expectedApi = kotlinApi(init)
        val actualApi = fixtureAsKotlinApi()
       if(updateFiles) writeToFile(actualApi)
        expectedApi assertEqualsTo actualApi
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


class TestException(problem: String) : Exception(problem)