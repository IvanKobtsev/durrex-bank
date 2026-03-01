package nekit.corporation.user.domain.model

import nekit.corporation.user.data.model.UserRole

data class User(
    val id: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val telephoneNumber: String,
    val role: UserRole,
    val isBlocked: Boolean
)
