package nekit.corporation.loan_shared.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.loan_shared.domain.model.Credit
import nekit.corporation.loan_shared.domain.model.Overdue
import nekit.corporation.loan_shared.domain.model.Rating
import nekit.corporation.loan_shared.domain.repository.CreditRepository

@Inject
class GetRatingUseCase(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(): Rating = repository.getRating()
}