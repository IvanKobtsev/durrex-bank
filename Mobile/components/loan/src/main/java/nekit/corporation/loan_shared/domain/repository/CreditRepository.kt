package nekit.corporation.loan_shared.domain.repository

import nekit.corporation.loan_shared.domain.model.Credit
import nekit.corporation.loan_shared.domain.model.CreditDetail

interface CreditRepository {

    suspend fun issueCredit(
        accountId: Int,
        tariffId: Int,
        amount: Double
    ): Credit

    suspend fun getCredits(
        clientId: Int
    ): List<Credit>

    suspend fun getCreditDetail(
        userId: Int,
        userRole: String,
        creditId: Int
    ): CreditDetail

    suspend fun repayCredit(
        userId: Int,
        userRole: String,
        creditId: Int
    ): Credit
}