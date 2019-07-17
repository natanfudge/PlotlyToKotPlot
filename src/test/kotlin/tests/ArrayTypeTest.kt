package tests

import org.junit.jupiter.api.Test
import p2kotplot.KotlinWriter
import util.fixture

class ArrayTypeTest {
    @Test
    fun interfaceArray() {
        fixture(category = "array", name = "interfaceArray") {
            expectedDeclarationFile {
                function("test") {
                    parameter(name = "interfaces", type = util.array("TestInterface"))
                }
                anInterface(name = "TestInterface") {
                    property(name = "stringProp", type = "string")
                    property(name = "numberProp", type = "number")
                }
            }

            expectedKotlinApi {
                topLevelFunction(name = "test", hasInitParam = true)
                builderClass(name = "TestInterfaceBuilder") {
                    arrayField("interfaces")
                    builderFunction(
                        name = KotlinWriter.SingularOfArrayFunctionPrefix + "interfaces",
                        hasInitParam = false,
                        builderNameOfConstructedType = "???"
                    ) {
//                        parameter(name = "")
                    }

                    arrayApplyStatement(variableName = "interfaces")
                }
            }
        }
    }
}