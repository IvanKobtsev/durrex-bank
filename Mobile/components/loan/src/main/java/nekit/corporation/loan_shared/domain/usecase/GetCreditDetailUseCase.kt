package nekit.corporation.loan_shared.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.loan_shared.domain.repository.CreditRepository

@Inject
class GetCreditDetailUseCase(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(
        creditId: Int
    ) = repository.getCreditDetail(creditId)
}