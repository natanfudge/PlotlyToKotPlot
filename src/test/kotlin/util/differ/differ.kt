package util.differ

import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.test.assertEquals

//import kotlin.reflect.full.memberProperties
//import kotlin.reflect.typeOf
//import kotlin.test.assertEquals
//import com.sun.javafx.fxml.BeanAdapter.getGenericType
//

//fun Any?.c(){}

//@ExperimentalStdlibApi
//inline fun <reified T : Any> preciseAssertEquals(obj1: T, obj2: T) {
//    preciseAssertEquals( obj1, obj2, DiffContext())
//}

@ExperimentalStdlibApi
fun preciseAssertEquals(obj1: Any?, obj2: Any?, context: DiffContext) {
    if (obj1 != null && obj2 != null) {
        context.assertTypeEquals(obj1, obj2)
        val kClass = obj1::class
        val name = kClass.simpleName
        when (name) {
            "List" -> preciseAssertListEquals(obj1, obj2, context)

            "String", "Boolean", "Int", "Float", "Char", "Double", "Byte", "Short" -> {
                context.assertPrimitiveEquals(obj1, obj2)
            }

            else -> {
                for (property in kClass.memberProperties) {
                    @Suppress("UNCHECKED_CAST")
                    property as KProperty1<Any, *>
                    preciseAssertEquals(property.get(obj1), property.get(obj2), DiffContext())
                }
            }
        }
    } else {
        //TODO: handle this case
    }


}

fun <T> incrementProperty(
    instance: Any, prop: KMutableProperty1<Any, *>
) {
    val value = prop.get(instance)
//    prop.set(instance, value + 1)
}

@ExperimentalStdlibApi
private fun preciseAssertListEquals(obj1: Any?, obj2: Any?, context: DiffContext) {
    obj1 as List<*>
    obj2 as List<*>
    context.assertLengthEquals(obj1, obj2)


    for (i in 0 until obj1.size) {
        val element1 = obj1[i]
        val element2 = obj2[i]


        preciseAssertEquals(element1, element2, DiffContext())


    }
}

class DiffContext {
    fun assertTypeEquals(obj1: Any?, obj2: Any?) {
        //TODO
    }

    fun assertLengthEquals(list1: List<*>, list2: List<*>) {
        //TODO
    }

    fun assertPrimitiveEquals(primitive1: Any?, primitive2: Any?) {
        assertEquals(primitive1, primitive2)
    }
}

