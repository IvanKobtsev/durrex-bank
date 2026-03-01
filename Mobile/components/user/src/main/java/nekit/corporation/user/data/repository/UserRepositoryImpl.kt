package nekit.corporation.user.data.repository

import nekit.corporation.user.data.model.toDomain
import nekit.corporation.user.data.model.toUser
import nekit.corporation.user.data.remote.Api
import nekit.corporation.user.domain.UserRepository
import nekit.corporation.user.domain.model.PagedTransactions
import nekit.corporation.user.domain.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: Api
) : UserRepository {

    override suspend fun getUser(): User {
        return api.getUser().toUser()
    }

    override suspend fun getTransactions(
        accountId: Int,
        page: Int,
        pageSize: Int
    ): PagedTransactions =
        api.getTransactions(accountId, page, pageSize).toDomain()
}