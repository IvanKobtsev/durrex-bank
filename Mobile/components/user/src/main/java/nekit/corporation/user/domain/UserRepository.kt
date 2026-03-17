package nekit.corporation.user.domain

import nekit.corporation.user.domain.model.User

interface UserRepository {

    suspend fun getUser(): User

}