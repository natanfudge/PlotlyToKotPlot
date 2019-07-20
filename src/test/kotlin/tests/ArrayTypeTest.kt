package tests

import org.junit.jupiter.api.Test
import util.fixture

fun main() {
    ArrayTypeTest().primitive()
}

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
                builderClass(name = "TestBuilder") {
                    arrayBuilderFunction(
                        arrayName = "interfaces",
                        hasInitParam = false,
                        constructedTypeBuilderName = "TestInterfaceBuilder"
                    ) {
                        parameter(name = "stringProp", type = "String")
                        parameter(name = "numberProp", type = "Number")
                    }

                }

                builderClass("TestInterfaceBuilder") {
                    constructorArgument(name = "stringProp", type = "String")
                    constructorArgument(name = "numberProp", type = "Number")
                }
            }
        }
    }


    @Test
    fun primitive() {
        fixture(category = "array", name = "primitive") {
            expectedDeclarationFile {
                function("test") {
                    parameter(name = "interfaces", type = util.array("string"))
                }
            }

            expectedKotlinApi {
                topLevelFunction(name = "test", hasInitParam = true)
                builderClass(name = "TestBuilder") {
                    arrayBuilderFunction(
                        arrayName = "interfaces",
                        hasInitParam = false,
                        constructedTypeBuilderName = null
                    ) {
                        parameter(name = "interfaces", type = "String")
                    }
                }
            }
        }
    }
}