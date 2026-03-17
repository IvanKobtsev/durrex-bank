package nekit.corporation.loan_shared.domain.usecase

import nekit.corporation.loan_shared.domain.repository.CreditRepository
import javax.inject.Inject

class GetCreditsUseCase @Inject constructor(
    private val repository: CreditRepository
) {

    suspend operator fun invoke(
        clientId: Int = 2
    ) = repository.getCredits(clientId)
}