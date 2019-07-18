package util.kotlinApiBuilders

inline fun kotlinApi(init: KotlinApiBuilder.() -> Unit) = KotlinApiBuilder().apply(init).build()