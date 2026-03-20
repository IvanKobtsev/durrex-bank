package nekit.corporation.user.domain

import nekit.corporation.user.domain.model.Settings
import nekit.corporation.user.domain.model.User

interface UserRepository {

    suspend fun getUser(): User

    suspend fun getUser(id: Int): User

    suspend fun getSettings(): Settings

    suspend fun saveSettings(settings: Settings)
}