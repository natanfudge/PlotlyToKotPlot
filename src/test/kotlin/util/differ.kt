package util

import kotlin.reflect.full.memberProperties
import kotlin.reflect.typeOf
import kotlin.test.assertEquals
import com.sun.javafx.fxml.BeanAdapter.getGenericType


//
//@ExperimentalStdlibApi
//inline fun <reified T : Any> preciseAssertEquals(obj1: T, obj2: T) {
//    if (T::class.simpleName == "List") {
//        val obj1AsList = obj1 as List<*>
//        val obj2AsList = obj2 as List<*>
//        assertEquals(obj1AsList.size, obj2AsList.size, "Size is not equal")
//
//        for(i in 0 until obj1AsList.size){
//            val element1 = obj1AsList[i]
//            val element2 = obj2AsList[i]
//
//            preciseAssertEquals<Any>(element1,element2)
//        }
////        for(element in obj1AsList){
////
////        }
//    }
//    print(T::class.simpleName)
////    if(T::class.simpleName == )
//////    val kClass : Class<T>
////
//////    kClass.na
////
//////    if(typeOf<T>(). == List<*>){
//////
//////    }
////    val obj1Props = T::class.memberProperties
//}
//
////@ExperimentalStdlibApi
//@ExperimentalStdlibApi
//fun main() {
//    diff(listOf(1), listOf(2))
//}
//
//class X