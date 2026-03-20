package nekit.corporation.loan_shared.domain.usecase

import nekit.corporation.loan_shared.domain.model.Account
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    val accountRepository: AccountRepository
) {

    suspend operator fun invoke(): List<Account> {
        return accountRepository.getAllAccounts()
    }
}