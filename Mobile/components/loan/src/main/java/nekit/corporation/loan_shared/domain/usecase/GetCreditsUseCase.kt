package nekit.corporation.loan_shared.domain.usecase

import nekit.corporation.loan_shared.domain.repository.CreditRepository

class GetCreditsUseCase(
    private val repository: CreditRepository
) {

    suspend operator fun invoke(
        clientId: Int
    ) = repository.getCredits( clientId)
}