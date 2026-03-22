package nekit.corporation.profile.model

internal data class AccountModel(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val isBlocked: Boolean,
    val rating: Float
)
