package nekit.corporation.user.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    @SerialName("Client")
    CLIENT,

    @SerialName("Admin")
    ADMIN,

    @SerialName("Manager")
    MANAGER;
}