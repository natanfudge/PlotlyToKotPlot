package util.differ

import org.junit.jupiter.api.Test

class DifferTest {
    @ExperimentalStdlibApi
    @Test
    fun primitives() {
        preciseAssertEquals(1, 2)
    }

    data class Simple(val str: String)
    data class MoreComplex(val simpleProp: Simple, val boolProp: Boolean)

    data class ListOfComplex(val list: List<MoreComplex>, val num: Int)

    @ExperimentalStdlibApi
    @Test
    fun simpleClass() {
        preciseAssertEquals(Simple("as"), Simple("a"))
    }

    @ExperimentalStdlibApi
    @Test
    fun moreComplexClassStr() {
        preciseAssertEquals(MoreComplex(Simple("as"), false), MoreComplex(Simple("a"), false))
    }

    @ExperimentalStdlibApi
    @Test
    fun moreComplexClassBool() {
        preciseAssertEquals(MoreComplex(Simple("as"), false), MoreComplex(Simple("as"), true))
    }

    @ExperimentalStdlibApi
    @Test
    fun singluarListOfComplexClass() {
        val value1 = ListOfComplex(
            listOf(
                MoreComplex(
                    Simple("a"), false
                )
            ), 2
        )
        val value2 = ListOfComplex(
            listOf(
                MoreComplex(
                    Simple("as"), false
                )
            ), 2
        )

        preciseAssertEquals(
            value1,value2
        )
    }

        @ExperimentalStdlibApi
    @Test
    fun lengthNotMatching() {
        val value1 = ListOfComplex(
            listOf(
                MoreComplex(
                    Simple("a"), false
                )
            ), 2
        )
        val value2 = ListOfComplex(
            listOf(
                MoreComplex(
                    Simple("a"), false
                ),
            MoreComplex(
                    Simple("a"), false
                )
            ), 2
        )

        preciseAssertEquals(
            value1,value2
        )
    }

}

