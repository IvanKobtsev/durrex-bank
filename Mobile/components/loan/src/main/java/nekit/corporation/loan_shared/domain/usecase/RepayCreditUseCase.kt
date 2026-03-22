package nekit.corporation.loan_shared.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.loan_shared.domain.repository.CreditRepository

@Inject
class RepayCreditUseCase(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(
        userId: Int,
        userRole: String,
        creditId: Int
    ) = repository.repayCredit(userId, userRole, creditId)
}