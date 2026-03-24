package nekit.corporation.user.domain.model

data class Settings(
    val hiddenAccountIds: List<Int>,
    val theme: Scheme
) {
    companion object {
        val default = Settings(
            hiddenAccountIds = emptyList(),
            theme = Scheme.light
        )
    }
}
