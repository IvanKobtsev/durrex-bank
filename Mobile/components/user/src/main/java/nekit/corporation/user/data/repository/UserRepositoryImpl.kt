package nekit.corporation.user.data.repository

import nekit.corporation.user.data.model.toUser
import nekit.corporation.user.data.remote.UserApi
import nekit.corporation.user.domain.UserRepository
import nekit.corporation.user.domain.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UserApi
) : UserRepository {

    override suspend fun getUser(): User {
        return api.getUser().toUser()
    }
}