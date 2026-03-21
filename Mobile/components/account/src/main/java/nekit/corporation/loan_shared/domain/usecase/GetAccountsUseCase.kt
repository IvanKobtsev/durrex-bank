package nekit.corporation.loan_shared.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.loan_shared.domain.model.Account
import nekit.corporation.loan_shared.domain.repository.AccountRepository

@Inject
class GetAccountsUseCase (
    val accountRepository: AccountRepository
) {

    suspend operator fun invoke(): List<Account> {
        return accountRepository.getAllAccounts()
    }
}