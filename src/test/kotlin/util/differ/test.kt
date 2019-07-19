package util.differ

import org.junit.jupiter.api.Test

class DifferTest {
    @ExperimentalStdlibApi
    @Test
    fun primitives() {
        preciseAssertEquals(1, 2)
    }

    data class Simple(val str: String)

    @ExperimentalStdlibApi
    @Test
    fun simpleClass() {
        preciseAssertEquals(Simple("as"), Simple("a"))
    }
}

@ExperimentalStdlibApi
private inline fun <reified T : Any> preciseAssertEquals(obj1: T, obj2: T) {
    preciseAssertEquals( obj1, obj2, DiffContext())
}