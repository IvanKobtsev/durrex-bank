package nekit.corporation.user.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateThemeDto(
    val theme: String,
)
