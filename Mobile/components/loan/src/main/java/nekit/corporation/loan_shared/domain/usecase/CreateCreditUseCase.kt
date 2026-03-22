package nekit.corporation.loan_shared.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.loan_shared.domain.model.Credit
import nekit.corporation.loan_shared.domain.repository.CreditRepository

@Inject
class CreateCreditUseCase(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(
        accountId: Int,
        tariffId: Int,
        amount: Double
    ): Credit =
        repository.issueCredit(accountId, tariffId, amount)
}