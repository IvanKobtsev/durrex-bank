package nekit.corporation.loan_shared.domain.repository

import kotlinx.coroutines.flow.Flow
import nekit.corporation.loan_shared.domain.model.Account
import nekit.corporation.loan_shared.domain.model.CreateAccount
import nekit.corporation.loan_shared.domain.model.Debit
import nekit.corporation.loan_shared.domain.model.Deposit
import nekit.corporation.loan_shared.domain.model.Transaction
import nekit.corporation.loan_shared.domain.model.Transfer
import nekit.corporation.loan_shared.domain.model.Withdraw

interface AccountRepository {

    suspend fun createAccount(model: CreateAccount)

    suspend fun getAccount(id: Int): Account

    suspend fun closeAccount(id: Int)

    suspend fun getAllAccounts(): List<Account>

    suspend fun createTransfer(accountId: Int, transfer: Transfer): Transaction

    suspend fun withdraw(accountId: Int, withdraw: Withdraw)

    suspend fun deposit(accountId: Int, deposit: Deposit)

    suspend fun debit(accountId: Int, debit: Debit)

    suspend fun getTransactions(accountId: Int): List<Transaction>

    fun getTransactionHubEvents():  Flow<Result<Unit>>
}