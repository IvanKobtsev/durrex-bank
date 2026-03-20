package nekit.corporation.loan_shared.data.repository

import jakarta.inject.Inject
import nekit.corporation.loan_shared.data.datasource.remote.api.AccountsApi
import nekit.corporation.loan_shared.data.datasource.remote.model.toAccount
import nekit.corporation.loan_shared.data.datasource.remote.model.toCreateAccountCommand
import nekit.corporation.loan_shared.data.datasource.remote.model.toDebitRequest
import nekit.corporation.loan_shared.domain.model.Account
import nekit.corporation.loan_shared.domain.model.CreateAccount
import nekit.corporation.loan_shared.domain.model.Deposit
import nekit.corporation.loan_shared.domain.model.Withdraw
import nekit.corporation.loan_shared.data.datasource.remote.model.toDepositRequest
import nekit.corporation.loan_shared.data.datasource.remote.model.toDomain
import nekit.corporation.loan_shared.data.datasource.remote.model.toTransferRequest
import nekit.corporation.loan_shared.data.datasource.remote.model.toWithdrawRequest
import nekit.corporation.loan_shared.domain.model.Debit
import nekit.corporation.loan_shared.domain.model.Transaction
import nekit.corporation.loan_shared.domain.model.Transfer
import nekit.corporation.loan_shared.domain.repository.AccountRepository


class AccountRepositoryImpl @Inject constructor(
    private val api: AccountsApi
) : AccountRepository {

    override suspend fun createAccount(model: CreateAccount) {
        api.createAccount(model.toCreateAccountCommand())
    }

    override suspend fun getAccount(id: Int): Account {
        return api.getAccount(id).toAccount()
    }

    override suspend fun closeAccount(id: Int) {
        api.deleteAccount(id)
    }

    override suspend fun getAllAccounts(): List<Account> {
        return api.getAccounts().map { it.toAccount() }
    }

    override suspend fun createTransfer(accountId: Int, transfer: Transfer): Transaction {
        return api.transfer(accountId, transfer.toTransferRequest()).toDomain()
    }

    override suspend fun withdraw(accountId: Int, withdraw: Withdraw) {
        api.withdraw(accountId, withdraw.toWithdrawRequest())
    }

    override suspend fun deposit(accountId: Int, deposit: Deposit) {
        api.deposit(accountId, deposit.toDepositRequest())
    }

    override suspend fun debit(accountId: Int, debit: Debit) {
        api.debit(accountId, debit.toDebitRequest())
    }

    override suspend fun getTransactions(accountId: Int): List<Transaction> {
        return api.getTransactions(accountId).items.map { it.toDomain() }
    }
}
