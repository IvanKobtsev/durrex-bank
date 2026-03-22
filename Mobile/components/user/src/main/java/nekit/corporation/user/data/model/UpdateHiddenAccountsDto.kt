package nekit.corporation.user.data.model

data class UpdateHiddenAccountsDto(
    val add: List<Int>,
    val hide: List<Int>
)
