package util.differ

import java.lang.AssertionError
import java.lang.StringBuilder
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

//import kotlin.reflect.full.memberProperties
//import kotlin.reflect.typeOf
//import kotlin.test.assertEquals
//import com.sun.javafx.fxml.BeanAdapter.getGenericType
//

//fun Any?.c(){}
//
//@ExperimentalStdlibApi
//inline fun <reified T : Any> preciseAssertEquals(obj1: T, obj2: T) {
//    preciseAssertEquals( obj1, obj2, DiffContext())
//}
@ExperimentalStdlibApi
inline fun <reified T : Any> preciseAssertEquals(expected: T, actual: T) {
    preciseAssertEquals(
        expected,
        actual,
        DiffContext(listOf(TraceNode(T::class.simpleName!!, primitiveValuesOf(actual)))),
        T::class.simpleName!!
    )
}


@ExperimentalStdlibApi
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
                    val actualValue = property.get(actual)!!

//                    val primitiveValues = primitiveValuesOf(property)
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

fun primitiveValuesOf(propertyToGetPrimitivesOf: Any): Map<String, String> {
    if (propertyToGetPrimitivesOf.isPrimitive()) return mapOf()
    if (propertyToGetPrimitivesOf is List<*>) return mapOf("size" to propertyToGetPrimitivesOf.size.toString())

    val map = mutableMapOf<String, String>()
    val kClass = propertyToGetPrimitivesOf::class
    for (property in kClass.memberProperties) {
        @Suppress("UNCHECKED_CAST")
        property as KProperty1<Any, *>
        val propertyValue = property.get(propertyToGetPrimitivesOf)
        if (propertyValue!!.isPrimitive()) {
            map[property.name] = propertyValue.toString()
        }
    }
    return map
}

private fun Any.isPrimitive() =
    this::class.simpleName in listOf("String", "Boolean", "Int", "Float", "Char", "Double", "Byte", "Short")

private fun Any.isList() = this::class.simpleName == "List"


@ExperimentalStdlibApi
private fun preciseAssertListEquals(expected: Any?, actual: Any?, context: DiffContext, listName: String) {
    expected as List<*>
    actual as List<*>
    context.assertLengthEquals(expected, actual, listName)


    for (i in 0 until expected.size) {
        val expectedElement = expected[i]
        val actualElement = actual[i]!!


        preciseAssertEquals(
            expectedElement,
            actualElement,
            context.inList(position = i, primitiveValues = primitiveValuesOf(actualElement)),
            listName
        )


    }
}

//const val arrow = '\u21b3'
const val arrow = "\u2BA1"

class DiffContext(private val trace: List<TraceNode>) {
    fun inList(position: Int, primitiveValues: Map<String, String>) = DiffContext(
        trace + TraceNode(name = position.toString(), primitiveData = primitiveValues/*, value = null*/)
    )

    fun inProperty(propertyName: String, primitiveValues: Map<String, String>) =
        DiffContext(trace + TraceNode(propertyName, primitiveValues))

    fun assertTypeEquals(expected: Any, actual: Any, name: String) {
        if (expected is List<*> && actual is List<*>) return
        if (expected::class != actual::class) {
            throwError(
                message = "$name type does not match",
                expected = expected::class.simpleName,
                actual = actual::class.simpleName
            )
        }
    }

    fun assertLengthEquals(expected: List<*>, actual: List<*>, listName: String) {
        if (expected.size != actual.size) {
            throwError(message = "$listName size is not equal", expected = expected.size, actual = actual.size)
//            throw AssertionError("$listName size is not equal. Expected: ${expected.size}")
        }
    }

    fun assertPrimitiveEquals(expected: Any, actual: Any, name: String) {
        if (expected != actual) throwError(message = "$name is not equal", expected = expected, actual = actual)

//            throw AssertionError(
//            "$name is not equal. Expected: <$expected>, Actual: <$actual>.\n"
//                    + "At:\t" + traceString() + " = $actual != $expected" + "\n\n\n\n\n\n\n\n\n"
//        )
    }

    fun assertNullabilityEquals(expected: Any?, actual: Any?, name: String) {
        if (expected != actual) {
            val message =
                if (expected == null) "Expected is null while actual is not" else "Actual is null while expected is not"
            throwError(message, expected = null, actual = null)
        }
    }

    /**
     * If expected is null we assume actual is null too and we don't represent the expected and actual.
     */
    private fun <T> throwError(message: String, expected: T?, actual: T?) {
        var error = "$message. "
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
//        return if(value != null) "$name = $value"
//        else name
    }
}

