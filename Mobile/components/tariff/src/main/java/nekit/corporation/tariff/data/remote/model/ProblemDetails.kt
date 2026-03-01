package nekit.corporation.tariff.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ProblemDetails(
    val type: String? = null,
    val title: String? = null,
    val status: Int? = null,
    val detail: String? = null,
    val instance: String? = null,
    val extensions: Map<String, kotlinx.serialization.json.JsonElement> = emptyMap()
) {
    @Serializable
    companion object {
        private val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
        fun fromJsonString(jsonString: String): ProblemDetails = json.decodeFromString(jsonString)
    }
}