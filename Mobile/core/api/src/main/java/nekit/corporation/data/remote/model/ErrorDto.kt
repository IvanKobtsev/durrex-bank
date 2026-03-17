package nekit.corporation.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
internal data class ErrorDto(
    val message: String? = null
)