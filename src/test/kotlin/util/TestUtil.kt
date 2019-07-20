package util

import p2kotplot.KotlinApi
import p2kotplot.plotlytypes.DeclarationFile
import p2kotplot.toKotlinApi
import p2kotplot.util.GsonTest
import p2kotplot.writeTo
import util.differ.preciseAssertEquals
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
const val nodeCommand = "node"
const val plotly2JsonSourceLocation = "src/ts/plotly2json.ts"
const val plotly2JsonBinLocation = "out/plotly2json.js"
//TODO: change to 'refresh fixture cache' and document

/**
 * Run the typescript converter even if there are no changes in the declaration file fixture
 */
const val forceTypescriptRerun = false

class FixtureContext(fixtureName: String, private val fixtureCategory: String) {
    private val targetLocation = "$fixtureCategory/$fixtureName"

    private val fixtureDeclarationFile: DeclarationFile
    private val decFileJsonLocation = "src/test/out/json/$targetLocation.json"
    private val kotlinApiLocation = "src/test/out/$targetLocation.kt"
    private val typescriptFixtureLocation = "src/test/fixtures/$targetLocation.d.ts"
    private val typescriptFixtureCacheLocation = "src/test/out/fixture_cache/$targetLocation.d.ts"


    init {
        // Generate and parse declaration file
        generateDeclarationFile()
        val declarationFile = File(decFileJsonLocation).readText()
        fixtureDeclarationFile = GsonTest.gson.fromJson(declarationFile, DeclarationFile::class.java)
    }


    private fun generateDeclarationFile() {
//        if (!File(targetFolder).exists()) File(targetFolder).mkdir()

        if (File(typescriptFixtureLocation).doesNotExist()) throw TestException("The fixture $typescriptFixtureLocation does not exist!")


        if (shouldRerunTypescriptConverter()) {
            "$nodeCommand $plotly2JsonBinLocation $typescriptFixtureLocation $decFileJsonLocation".runCommand()
            File(typescriptFixtureLocation).copyTo(File(typescriptFixtureCacheLocation), overwrite = true)
        }

    }

    private fun shouldRerunTypescriptConverter(): Boolean {
        val fixtureCache = File(typescriptFixtureCacheLocation)
        val handWrittenFixture = File(typescriptFixtureLocation)
        if (fixtureCache.doesNotExist() || handWrittenFixture.doesNotExist()) return true
        val cacheText = fixtureCache.readText()
        val handWrittenText = handWrittenFixture.readText()
        return cacheText != handWrittenText || forceTypescriptRerun
    }

    fun expectedDeclarationFile(init: DeclarationFileBuilder.() -> Unit) {
        val expectedDeclarationFile = declarationFile(init)
        preciseAssertEquals(expectedDeclarationFile, fixtureDeclarationFile)
//        expectedDeclarationFile assertEqualsTo fixtureDeclarationFile
    }

    fun expectedKotlinApi(init: KotlinApiBuilder.() -> Unit) {
        val expectedApi = kotlinApi(init)
        val actualApi = fixtureDeclarationFile.toKotlinApi()
        actualApi.writeTo(kotlinApiLocation)
        preciseAssertEquals(expectedApi,actualApi)
//        expectedApi assertEqualsTo actualApi
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