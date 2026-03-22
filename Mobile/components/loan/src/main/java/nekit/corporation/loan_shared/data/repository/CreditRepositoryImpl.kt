package nekit.corporation.loan_shared.data.repository

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import nekit.corporation.loan_shared.data.datasource.remote.api.LoanApi
import nekit.corporation.loan_shared.data.datasource.remote.model.IssueCreditRequest
import nekit.corporation.loan_shared.data.datasource.remote.model.toDomain
import nekit.corporation.loan_shared.domain.model.Credit
import nekit.corporation.loan_shared.domain.model.CreditDetail
import nekit.corporation.loan_shared.domain.model.Overdue
import nekit.corporation.loan_shared.domain.model.Rating
import nekit.corporation.loan_shared.domain.repository.CreditRepository

@Inject
@ContributesBinding(AppScope::class)
class CreditRepositoryImpl(
    private val api: LoanApi
) : CreditRepository {

    override suspend fun issueCredit(
        accountId: Int,
        tariffId: Int,
        amount: Double
    ): Credit =
        api.issueCredit(
            IssueCreditRequest(accountId, tariffId, amount)
        ).toDomain()

    override suspend fun getCredits(
        clientId: Int
    ): List<Credit> =
        api.getCredits(clientId).map { it.toDomain() }

    override suspend fun getCreditDetail(
        creditId: Int
    ): CreditDetail =
        api.getCreditDetail(creditId).toDomain()

    override suspend fun repayCredit(
        userId: Int,
        userRole: String,
        creditId: Int
    ): Credit =
        api.repayCredit(userId).toDomain()

    override suspend fun getOverdueCredits(): List<Overdue> {
        return api.getOverdueCredits().map { it.toDomain() }
    }

    override suspend fun getRating(): Rating {
        return api.getRating().toDomain()
    }
}
