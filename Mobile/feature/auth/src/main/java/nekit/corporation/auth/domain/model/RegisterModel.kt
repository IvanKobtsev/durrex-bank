package nekit.corporation.auth.domain.model

data class RegisterModel(
    val email: String,
    val login: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phone: String
)
