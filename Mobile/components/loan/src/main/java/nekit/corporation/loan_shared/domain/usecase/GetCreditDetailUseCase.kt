package nekit.corporation.loan_shared.domain.usecase

import jakarta.inject.Inject
import nekit.corporation.loan_shared.domain.repository.CreditRepository

class GetCreditDetailUseCase @Inject constructor(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(
        creditId: Int
    ) = repository.getCreditDetail(creditId)
}