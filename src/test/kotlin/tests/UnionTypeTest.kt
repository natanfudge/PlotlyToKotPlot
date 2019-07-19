package tests

import org.junit.jupiter.api.Test
import p2kotplot.plotlytypes.*
import util.array
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
    fun array() = fixture(category = "union", name = "array") {
        expectedDeclarationFile {
            function("testPrimitive") {
                parameter(
                    name = "param",
                    type = union(
                        array("string"),
                        ParameterizedType(name = "Array", typeArguments = listOf(ReferenceType("number")))
                    )
                )
            }

                function("testHalfInterface") {
                    parameter(name = "param", type = union(array("string"), array("Interface1")))
                }

//                function("testInterface") {
//                    parameter(name = "param", type = union(array("Interface1"), array("Interface2")))
//                }
//
//                function("testTwoTypes") {
//                    parameter(name = "param", type = array(union("string", "boolean")))
//                }
//
//                function("testUnionOfTwoTypes") {
//                    parameter(name = "param", type = union(array(union("string", "boolean")), array("Interface1")))
//                }

            anInterface(name = "Interface1") {
                property(name = "prop", type = "string")
            }
//
//            anInterface(name = "Interface2") {
//                property("prop", type = "boolean")
//            }

        }
        //TODO: does not work
        expectedKotlinApi {
            topLevelFunction(name = "testPrimitive", hasInitParam = true)
                topLevelFunction(name = "testHalfInterface", hasInitParam = true)
//                topLevelFunction(name = "testInterface", hasInitParam = true)
//                topLevelFunction(name = "testTwoTypes", hasInitParam = true)
//                topLevelFunction(name = "testUnionOfTwoTypes", hasInitParam = true)

            builderClass("Interface1Builder") {
                constructorArgument(name = "prop", type = "string")
            }
//
//            builderClass("Interface2Builder") {
//                constructorArgument(name = "prop", type = "boolean")
//            }

            builderClass("TestPrimitiveBuilder") {
                arrayBuilderFunction(arrayName = "param", hasInitParam = false, constructedTypeBuilderName = null) {
                    parameter(name = "param", type = "string")
                }

                arrayBuilderFunction(arrayName = "param", hasInitParam = false, constructedTypeBuilderName = null) {
                    parameter(name = "param", type = "number")
                }
            }

            builderClass("TestHalfInterfaceBuilder") {
                arrayBuilderFunction(arrayName = "param", hasInitParam = false, constructedTypeBuilderName = null) {
                    parameter(name = "param", type = "string")
                }

                arrayBuilderFunction(arrayName = "param", hasInitParam = true, constructedTypeBuilderName = "Interface1Builder") {
                    parameter(name = "param", type = "Interface1")
                }
            }

        }
    }


    @Test
    fun twoInterfaces() {
        fixture(category = "union", name = "twoInterfaces") {
            expectedDeclarationFile {
                function(name = "test") {
                    parameter(name = "param", type = union("Interface1", "Interface2"))
                }
                anInterface(name = "Interface1") {
                    property(name = "prop", type = "string")
                }
                anInterface(name = "Interface2") {
                    property(name = "prop", type = "boolean")
                }
            }

            expectedKotlinApi {
                topLevelFunction(name = "test", hasInitParam = true)

                builderClass("TestBuilder") {
                    builderFunction(
                        name = "param",
                        hasInitParam = false,
                        builderNameOfConstructedType = "Interface1Builder"
                    ) {
                        parameter(name = "prop", type = "String")
                    }

                    builderFunction(
                        name = "param",
                        hasInitParam = false,
                        builderNameOfConstructedType = "Interface2Builder"
                    ) {
                        parameter(name = "prop", type = "Boolean")
                    }
                }

                builderClass("Interface1Builder") {
                    constructorArgument(name = "prop", type = "String")
                }

                builderClass("Interface2Builder") {
                    constructorArgument(name = "prop", type = "Boolean")
                }
            }
        }
    }
}