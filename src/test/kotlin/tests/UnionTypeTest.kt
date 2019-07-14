package tests

import org.junit.jupiter.api.Test
import p2kotplot.plotlytypes.UnionType
import util.declarationFile
import util.fixture
import util.isEqualTo
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

            val kotlin = generatedKotlinFile
//            val actualDeclarationFile = fixtureDecFile()
//            val expectedDeclarationFile = declarationFile {
//
//            }
//            actualDeclarationFile isEqualTo expectedDeclarationFile
        }
    }
}