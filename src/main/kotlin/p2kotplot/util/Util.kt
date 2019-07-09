package p2kotplot.util

fun <T>MutableList<T>.addIfNotIn(obj : T) = if(obj !in this) add(obj) else null