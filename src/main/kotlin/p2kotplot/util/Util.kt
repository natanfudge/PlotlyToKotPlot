package p2kotplot.util

fun <T> MutableList<T>.addIfNotIn(obj: T) = if (obj !in this) add(obj) else null

fun <T,U>Iterable<Iterable<T>>.deepMap(mapping : (T) -> U) : List<List<U>> = map { it.map(mapping) }

//fun <T, U> Iterable<T>.mapIndexed(mapping: (Int, T) -> U) {
//    val x = listOf(2).map
//}