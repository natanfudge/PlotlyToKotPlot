import kotlin.DslMarker
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonLiteral
import kotlinx.serialization.json.JsonObject

@DslMarker
annotation class Builder

@Builder
class TestBuilder() {
	private val jsonMap: MutableMap<String, JsonElement> = mutableMapOf()

	internal fun build(): JsonObject {
		jsonMap.apply {
		}
		return JsonObject(jsonMap)
	}
}

fun test() {
	val jsonObject = TestBuilder().build();print(jsonObject.asIterable().joinToString("\n"))
}
