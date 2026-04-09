package nekit.corporation.loan_shared.data.datasource.remote.api

import kotlinx.coroutines.flow.Flow

interface AccountHub {

    fun getTransactionHubEvents(accountId:Int): Flow<Result<Unit>>
}