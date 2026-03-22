package nekit.corporation.loan_shared.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.loan_shared.domain.model.Credit
import nekit.corporation.loan_shared.domain.model.Overdue
import nekit.corporation.loan_shared.domain.repository.CreditRepository

@Inject
class GetOverdueUseCase(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(): List<Overdue> = repository.getOverdueCredits()
}