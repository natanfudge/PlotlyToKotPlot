package tests

import de.danielbechler.diff.ObjectDifferBuilder
import util.fixture
import org.junit.jupiter.api.Test
import util.kotlinApi
import kotlin.test.assertEquals
import de.danielbechler.diff.node.Visit
import de.danielbechler.diff.node.DiffNode
import jdk.nashorn.internal.runtime.PropertyMap.diff
import p2kotplot.KotlinWriter
import util.array
import util.assertEqualsTo
import javax.lang.model.type.ArrayType
import javax.lang.model.type.ReferenceType


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

            expectedKotlinApi {
                topLevelFunction("test", hasInitParam = true) {
                    parameter(name = "stringParam", type = "String")
                    parameter(name = "booleanParam", type = "Boolean")
                    parameter(name = "numberParam", type = "Number")
                }
                builderClass(name = "TestBuilder") {
                    constructorArgument(name = "stringParam", type = "String")
                    constructorArgument(name = "booleanParam", type = "Boolean")
                    constructorArgument(name = "numberParam", type = "Number")

                    builderFunction(
                        name = "interfaceParam",
                        builderNameOfConstructedType = "TestInterfaceBuilder",
                        hasInitParam = false
                    ) {
                        parameter(name = "stringProp", type = "String")
                        parameter(name = "numberProp", type = "Number")
                    }

                }

                builderClass(name = "TestInterfaceBuilder"){
                    constructorArgument("stringProp", type = "String")
                    constructorArgument("numberProp", type = "Number")
                }
            }

        }
    }

    @Test
    fun primitive() {
        fixture(category = "reference", name = "primitive") {
            expectedDeclarationFile {
                function("test") {
                    parameter(name = "param", type = "string")
                }
            }

            expectedKotlinApi {
                topLevelFunction(
                    name = "test",
                    hasInitParam = false
                ) {
                    parameter(name = "param", type = "String")
                }

                builderClass(name = "TestBuilder") {
                    constructorArgument(name = "param", type = "String")
                }
            }

        }
    }


}

