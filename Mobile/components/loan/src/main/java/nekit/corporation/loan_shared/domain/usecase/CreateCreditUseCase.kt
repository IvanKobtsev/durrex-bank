package nekit.corporation.loan_shared.domain.usecase

import jakarta.inject.Inject
import nekit.corporation.loan_shared.domain.model.Credit
import nekit.corporation.loan_shared.domain.repository.CreditRepository

class CreateCreditUseCase @Inject constructor(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(
        accountId: Int,
        tariffId: Int,
        amount: Double
    ): Credit =
        repository.issueCredit(accountId, tariffId, amount)
}