package tests

import org.junit.jupiter.api.Test
import p2kotplot.plotlytypes.ArrayType
import p2kotplot.plotlytypes.LiteralType
import p2kotplot.plotlytypes.ReferenceType
import util.fixture
import util.union

class UnionTypeTest {
    @Test
    fun simpleTypes() {
        fixture(category = "union", name = "simpleTypes") {
            expectedDeclarationFile {
                function("test") {
                    parameter(name = "param", type = union("string", "number"))
                }
            }

            expectedKotlinApi {
                topLevelFunction(name = "test", hasInitParam = false) {
                    parameter(name = "param", type = "String")
                }
                topLevelFunction(name = "test", hasInitParam = false) {
                    parameter(name = "param", type = "Number")
                }

                builderClass(name = "TestBuilder") {
                    constructorArgument(name = "param", type = "Any")
                }

            }

        }
    }

    @Test
    fun simpleEnum() {
        fixture(category = "union", name = "simpleEnum") {
            expectedDeclarationFile {
                function("test") {
                    parameter(name = "param", type = union(LiteralType("a"), LiteralType("b")))
                }
            }

            expectedKotlinApi {
                topLevelFunction(name = "test", hasInitParam = false) {
                    parameter(name = "param", type = "AOrB")
                }

                builderClass(name = "TestBuilder") {
                    constructorArgument(name = "param", type = "AOrB", isEnum = true)
                }

                enum("AOrB") {
                    constant(name = "A", originalName = "a")
                    constant(name = "B", originalName = "b")
                }
            }
        }
    }

    @Test
    fun array() {
        fixture(category = "union", name = "array") {
            expectedDeclarationFile {
                function("test") {
                    parameter(
                        name = "param",
                        type = union(ArrayType(ReferenceType("String")), ArrayType(ReferenceType("Number")))
                    )
                }
            }

            expectedKotlinApi{
                topLevelFunction(name = "test", hasInitParam = true){
                }

                builderClass("TestBuilder"){
//                    builderFunction()
                }
            }
        }
    }
}