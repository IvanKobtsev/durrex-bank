package nekit.corporation.user.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val telephoneNumber: String,
    val role: UserRole,
    val isBlocked: Boolean
)
