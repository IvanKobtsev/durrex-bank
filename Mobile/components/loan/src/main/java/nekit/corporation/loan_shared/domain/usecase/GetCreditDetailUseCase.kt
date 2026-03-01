package nekit.corporation.loan_shared.domain.usecase

import nekit.corporation.loan_shared.domain.repository.CreditRepository

class GetCreditDetailUseCase(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(
        userId: Int,
        userRole: String,
        creditId: Int
    ) = repository.getCreditDetail(userId, userRole, creditId)
}