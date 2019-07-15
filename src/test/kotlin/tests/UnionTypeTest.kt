package tests

import org.junit.jupiter.api.Test
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
                topLevelFunction(name = "test", hasInitParam = false){
                    parameter(name = "param", type = "String")
                }
                topLevelFunction(name = "test",hasInitParam = false){
                    parameter(name = "param", type = "Number")
                }

                builderClass(name = "TestBuilder"){
                    constructorArgument(name = "param", type = "Any")
                }

            }

        }
    }
}