import org.junit.jupiter.api.Test
import java.io.File
import java.util.concurrent.TimeUnit


const val tscLocation = "C:\\Users\\natan\\AppData\\Roaming\\npm\\tsc.cmd"

class ReferenceTypeTest {

    @Test
    fun normal() {
        convertToJson("reference/normal")
//        "node out/plotly2json.js".runCommand()
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