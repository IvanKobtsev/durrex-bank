package nekit.corporation.loan_shared.domain.usecase

import jakarta.inject.Inject
import nekit.corporation.loan_shared.domain.repository.CreditRepository

class RepayCreditUseCase @Inject constructor(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(
        userId: Int,
        userRole: String,
        creditId: Int
    ) = repository.repayCredit(userId, userRole, creditId)
}