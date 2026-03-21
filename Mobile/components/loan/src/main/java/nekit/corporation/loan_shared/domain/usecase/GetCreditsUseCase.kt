package nekit.corporation.loan_shared.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.loan_shared.domain.repository.CreditRepository

@Inject
class GetCreditsUseCase(
    private val repository: CreditRepository
) {

    suspend operator fun invoke(
        clientId: Int = 2
    ) = repository.getCredits(clientId)
}