package nekit.corporation.loan_shared.data.datasource.remote.api

import nekit.corporation.loan_shared.data.datasource.remote.model.CreditDetailResponse
import nekit.corporation.loan_shared.data.datasource.remote.model.CreditResponse
import nekit.corporation.loan_shared.data.datasource.remote.model.IssueCreditRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LoanApi {

    @POST("credit/credits")
    suspend fun issueCredit(
        @Body request: IssueCreditRequest
    ): CreditResponse

    @GET("credit/credits")
    suspend fun getCredits(
        @Query("clientId") clientId: Int
    ): List<CreditResponse>

    @GET("credit/credits/{id}")
    suspend fun getCreditDetail(
        @Path("id") id: Int
    ): CreditDetailResponse

    @POST("credit/credits/{id}/repay")
    suspend fun repayCredit(
        @Path("id") id: Int
    ): CreditResponse
}