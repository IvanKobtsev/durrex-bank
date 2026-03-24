package nekit.corporation.loan_shared.domain.repository

import nekit.corporation.loan_shared.data.datasource.remote.model.OverdueDto
import nekit.corporation.loan_shared.data.datasource.remote.model.RatingDto
import nekit.corporation.loan_shared.domain.model.Credit
import nekit.corporation.loan_shared.domain.model.CreditDetail
import nekit.corporation.loan_shared.domain.model.Overdue
import nekit.corporation.loan_shared.domain.model.Rating
import retrofit2.http.GET

interface CreditRepository {

    suspend fun issueCredit(
        accountId: Int,
        tariffId: Int,
        amount: Double
    ): Credit

    suspend fun getCredits(): List<Credit>

    suspend fun getCreditDetail(
        creditId: Int
    ): CreditDetail

    suspend fun repayCredit(
        userId: Int,
        userRole: String,
        creditId: Int
    ): Credit

    suspend fun getOverdueCredits(): List<Overdue>

    suspend fun getRating(): Rating
}