package tests

import util.declarationFile
import util.fixture
import util.isEqualTo
import org.junit.jupiter.api.Test


const val tscLocation = "C:\\Users\\natan\\AppData\\Roaming\\npm\\tsc.cmd"

//TODO: documentation tests
//TODO: optional tests
//TODO: extends tests
class ReferenceTypeTest {

    @Test
    fun normal() {
        fixture(name = "normal", category = "reference") {
            expectedDeclarationFile {
                function("test") {
                    parameter(name = "interfaceParam", type = "TestInterface")
                    parameter(name = "stringParam", type = "string")
                    parameter(name = "booleanParam", type = "boolean")
                    parameter(name = "numberParam", type = "number")
                }
                anInterface(name = "TestInterface") {
                    property(name = "stringProp", type = "string")
                    property(name = "numberProp", type = "number")

                }
            }

            val kotlin = generatedKotlinFile
        }
    }
}

