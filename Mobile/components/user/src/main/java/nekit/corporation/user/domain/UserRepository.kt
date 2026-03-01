package nekit.corporation.user.domain

import nekit.corporation.user.domain.model.PagedTransactions
import nekit.corporation.user.domain.model.User

interface UserRepository {

    suspend fun getUser(): User

    suspend fun getTransactions(
        accountId: Int,
        page: Int = 1,
        pageSize: Int = 200
    ): PagedTransactions
}