package nekit.corporation.user.domain

import nekit.corporation.user.domain.model.Scheme
import nekit.corporation.user.domain.model.Settings
import nekit.corporation.user.domain.model.User

interface UserRepository {

    suspend fun getUser(): User

    suspend fun getUser(id: Int): User

    suspend fun getSettings(): Settings

    suspend fun saveSettings(settings: Settings): Settings

    suspend fun updateHidden(added: List<Int>, removed: List<Int>): Settings

    suspend fun updateTheme(theme: Scheme): Settings
}