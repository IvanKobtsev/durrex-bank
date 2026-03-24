package nekit.corporation.user.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateHiddenAccountsDto(
    val add: List<Int>,
    val hide: List<Int>
)
