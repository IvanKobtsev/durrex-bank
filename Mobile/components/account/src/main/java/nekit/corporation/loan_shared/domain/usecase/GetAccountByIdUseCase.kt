package nekit.corporation.loan_shared.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.loan_shared.domain.model.Account
import nekit.corporation.loan_shared.domain.repository.AccountRepository

@Inject
class GetAccountByIdUseCase(
    val accountRepository: AccountRepository
) {

    suspend operator fun invoke(id: Int): Account {
        return accountRepository.getAccount(id)
    }
}