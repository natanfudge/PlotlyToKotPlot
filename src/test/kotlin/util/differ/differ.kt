package util.differ

import java.lang.AssertionError
import java.lang.StringBuilder
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties


inline fun <reified T : Any> preciseAssertEquals(expected: T, actual: T) {
    preciseAssertEquals(
        expected,
        actual,
        DiffContext(listOf(TraceNode(T::class.simpleName!!, primitiveValuesOf(actual)))),
        T::class.simpleName!!
    )
}

//TODO: do a sorta layered approach
fun preciseAssertEquals(expected: Any?, actual: Any?, context: DiffContext, propertyName: String) {
    if (expected != null && actual != null) {
        context.assertTypeEquals(expected, actual, propertyName)
        val kClass = expected::class
        when {
            expected is List<*> -> preciseAssertListEquals(expected, actual, context, propertyName)

            expected.isPrimitive() -> {
                context.assertPrimitiveEquals(expected, actual, propertyName)
            }

            else -> {
                for (property in kClass.memberProperties) {
                    @Suppress("UNCHECKED_CAST")
                    property as KProperty1<Any, *>

                    val expectedValue = property.get(expected)
                    val actualValue = property.get(actual)

                    val primitiveValues = primitiveValuesOf(actualValue)


                    preciseAssertEquals(
                        expectedValue,
                        actualValue,
                        context.inProperty(property.name, primitiveValues),
                        property.name
                    )
                }
            }
        }
    } else {
        context.assertNullabilityEquals(expected, actual, propertyName)
    }


}

fun primitiveValuesOf(propertyToGetPrimitivesOf: Any?): Map<String, String> {
    if (propertyToGetPrimitivesOf == null || propertyToGetPrimitivesOf.isPrimitive()) return mapOf()
    if (propertyToGetPrimitivesOf is List<*>) return mapOf("size" to propertyToGetPrimitivesOf.size.toString())

    val map = mutableMapOf<String, String>()
    val kClass = propertyToGetPrimitivesOf::class
    for (property in kClass.memberProperties) {
        @Suppress("UNCHECKED_CAST")
        property as KProperty1<Any, *>
        val propertyValue = property.get(propertyToGetPrimitivesOf)
        if (propertyValue?.isPrimitive() != false) {
            map[property.name] = propertyValue.toString()
        }
    }
    return map
}

private fun Any.isPrimitive() =
    this::class.simpleName in listOf("String", "Boolean", "Int", "Float", "Char", "Double", "Byte", "Short")


private fun preciseAssertListEquals(expected: Any?, actual: Any?, context: DiffContext, listName: String) {
    expected as List<*>
    actual as List<*>
    context.assertLengthEquals(expected, actual, listName)


    for (i in 0 until expected.size) {
        val expectedElement = expected[i]
        val actualElement = actual[i]


        preciseAssertEquals(
            expectedElement,
            actualElement,
            context.inList(
                position = i,
                primitiveValues = primitiveValuesOf(actualElement),
                listElementType = if (actualElement != null) actualElement::class.simpleName!! else "null"
            ),
            listName
        )


    }
}

//const val arrow = '\u21b3'
const val arrow = "\u2BA1"

class DiffContext(private val trace: List<TraceNode>) {
    fun inList(position: Int, listElementType: String, primitiveValues: Map<String, String>) = DiffContext(
        trace + TraceNode(name = "$position <$listElementType>", primitiveData = primitiveValues/*, value = null*/)
    )

    fun inProperty(propertyName: String, primitiveValues: Map<String, String>) =
        DiffContext(trace + TraceNode(propertyName, primitiveValues))

    fun assertTypeEquals(expected: Any, actual: Any, name: String) {
        if (expected is List<*> && actual is List<*>) return
        if (expected::class != actual::class) {
            throwError(
                message = "$name type does not match. ",
                expected = expected::class.simpleName,
                actual = actual::class.simpleName
            )
        }
    }

    private fun <T> List<T>.difference(other: List<T>, iterableName: String): String {
        val missing = this.filter { it !in other }
        val extraneous = other.filter { it !in this }
        val missingError = if (missing.isNotEmpty()) "These $iterableName are missing: $missing\n" else ""
        val extraneousError =
            if (extraneous.isNotEmpty()) "These $iterableName are not supposed to exist: $extraneous\n" else ""
        return missingError + extraneousError
    }

    fun assertLengthEquals(expected: List<*>, actual: List<*>, listName: String) {
        if (expected.size != actual.size) {
            val difference = expected.difference(actual, iterableName = listName)
            throwError(message = "$listName size is not equal:\n $difference", expected = expected.size, actual = actual.size)
        }
    }

    fun assertPrimitiveEquals(expected: Any, actual: Any, name: String) {
        if (expected != actual) throwError(message = "$name is not equal. ", expected = expected, actual = actual)
    }

    fun assertNullabilityEquals(expected: Any?, actual: Any?, name: String) {
        if (expected != actual) {
            val message =
                if (expected == null) "Expected is null while actual is not. " else "Actual is null while expected is not. "
            throwError(message, expected = null, actual = null)
        }
    }

    /**
     * If expected is null we assume actual is null too and we don't represent the expected and actual.
     */
    private fun <T> throwError(message: String, expected: T?, actual: T?) {
        var error = message
        if (expected != null) {
            error += "Expected: <$expected>, Actual: <$actual>."
        }
        error += "\nAt:\t" + traceString() + " = $actual != $expected" + "\n\n\n\n\n\n\n\n\n"
        throw AssertionError(error)
    }

    private fun traceString(): String {
        val sb = StringBuilder()
        sb.append(trace[0])
        for (i in 1 until trace.size) {
            sb.append("\n" + "\t" * (i + 1) + "$arrow ${trace[i]}")

        }
        return sb.toString()
    }
}

private operator fun String.times(num: Int): String {
    val sb = StringBuilder()
    repeat(num) {
        sb.append(this)
    }
    return sb.toString()
}

data class TraceNode(val name: String, val primitiveData: Map<String, String>/* = mapOf()*//*, val value: String?*/) {
    override fun toString(): String {
        var str = name
        if (primitiveData.isNotEmpty()) str += " [" + primitiveData.entries.joinToString(",") { "${it.key} = ${it.value}" } + "]"
        return str
    }
}

