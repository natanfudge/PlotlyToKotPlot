import kotlin.DslMarker
import kotlin.Unit
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonLiteral
import kotlinx.serialization.json.JsonObject

@DslMarker
annotation class Builder

@Builder
class TestBuilder() {
	private val jsonMap: MutableMap<String, JsonElement> = mutableMapOf()

	fun param(prop: String) {
		jsonMap["param"] = Interface1Builder(prop).build()
	}

	fun param(prop: Boolean) {
		jsonMap["param"] = Interface2Builder(prop).build()
	}

	internal fun build(): JsonObject {
		jsonMap.apply {
		}
		return JsonObject(jsonMap)
	}
}

@Builder
class Interface1Builder(
	private val prop: String
) {
	private val jsonMap: MutableMap<String, JsonElement> = mutableMapOf()

	internal fun build(): JsonObject {
		jsonMap.apply {
			 jsonMap["prop"] = JsonLiteral(prop)
		}
		return JsonObject(jsonMap)
	}
}

@Builder
class Interface2Builder(
	private val prop: Boolean
) {
	private val jsonMap: MutableMap<String, JsonElement> = mutableMapOf()

	internal fun build(): JsonObject {
		jsonMap.apply {
			 jsonMap["prop"] = JsonLiteral(prop)
		}
		return JsonObject(jsonMap)
	}
}

fun test(init: TestBuilder.() -> Unit = {}): JsonObject = TestBuilder().apply(init).build()
