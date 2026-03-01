package nekit.corporation.loan_shared.data.repository

import jakarta.inject.Inject
import nekit.corporation.loan_shared.data.datasource.remote.api.AccountsApi
import nekit.corporation.loan_shared.data.datasource.remote.model.toAccount
import nekit.corporation.loan_shared.data.datasource.remote.model.toCreateAccountCommand
import nekit.corporation.loan_shared.domain.model.Account
import nekit.corporation.loan_shared.domain.model.CreateAccount
import nekit.corporation.loan_shared.domain.model.Deposit
import nekit.corporation.loan_shared.domain.model.Withdraw
import nekit.corporation.loan_shared.data.datasource.remote.model.toDepositRequest
import nekit.corporation.loan_shared.data.datasource.remote.model.toWithdrawRequest
import nekit.corporation.loan_shared.domain.repository.AccountRepository


class AccountRepositoryImpl @Inject constructor(
    private val api: AccountsApi
) : AccountRepository {

    override suspend fun createAccount(model: CreateAccount) {
        api.createAccount(model.toCreateAccountCommand())
    }

    override suspend fun closeAccount(id: Int) {
        api.deleteAccount(id)
    }

    override suspend fun getAllAccounts(ownerId: Int): List<Account> {
        return api.getAccounts(ownerId).map { it.toAccount() }
    }

    override suspend fun withdraw(accountId: Int, withdraw: Withdraw) {
        api.withdraw(accountId, withdraw.toWithdrawRequest())
    }

    override suspend fun deposit(accountId: Int, deposit: Deposit) {
        api.deposit(accountId, deposit.toDepositRequest())
    }
}
