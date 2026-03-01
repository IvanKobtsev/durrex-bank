package nekit.corporation.loan_shared.domain.repository

import nekit.corporation.loan_shared.domain.model.Account
import nekit.corporation.loan_shared.domain.model.CreateAccount
import nekit.corporation.loan_shared.domain.model.Deposit
import nekit.corporation.loan_shared.domain.model.Withdraw

interface AccountRepository {

    suspend fun createAccount(model: CreateAccount)

    suspend fun closeAccount(id: Int)

    suspend fun getAllAccounts(ownerId: Int): List<Account>

    suspend fun withdraw(accountId: Int, withdraw: Withdraw)

    suspend fun deposit(accountId: Int, deposit: Deposit)

}