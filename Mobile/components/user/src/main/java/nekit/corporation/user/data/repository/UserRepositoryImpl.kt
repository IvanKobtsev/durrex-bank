package nekit.corporation.user.data.repository

import nekit.corporation.user.data.model.toUser
import nekit.corporation.user.data.remote.UserApi
import nekit.corporation.user.domain.UserRepository
import nekit.corporation.user.domain.model.Settings
import nekit.corporation.user.domain.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {

    override suspend fun getUser(): User {
        return userApi.getUser().toUser()
    }

    override suspend fun getUser(id: Int): User {
        return userApi.getUserById(id).toUser()
    }

    override suspend fun getSettings(): Settings {
        TODO("Not yet implemented")
    }

    override suspend fun saveSettings(settings: Settings) {
        TODO("Not yet implemented")
    }
}