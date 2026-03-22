package nekit.corporation.user.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SettingsDto(
    val theme: String,
    val hiddenAccountIds: List<Int>
)
