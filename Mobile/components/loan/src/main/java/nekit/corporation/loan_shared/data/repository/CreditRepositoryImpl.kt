package nekit.corporation.loan_shared.data.repository

import nekit.corporation.loan_shared.data.datasource.remote.api.LoanApi
import nekit.corporation.loan_shared.data.datasource.remote.model.IssueCreditRequest
import nekit.corporation.loan_shared.data.datasource.remote.model.toDomain
import nekit.corporation.loan_shared.domain.model.Credit
import nekit.corporation.loan_shared.domain.model.CreditDetail
import nekit.corporation.loan_shared.domain.repository.CreditRepository
import javax.inject.Inject

class CreditRepositoryImpl @Inject constructor(
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
        userId: Int,
        userRole: String,
        creditId: Int
    ): CreditDetail =
        api.getCreditDetail(userId).toDomain()

    override suspend fun repayCredit(
        userId: Int,
        userRole: String,
        creditId: Int
    ): Credit =
        api.repayCredit(userId).toDomain()
}
