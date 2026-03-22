package nekit.corporation.loan_shared.data.datasource.remote.api

import nekit.corporation.loan_shared.data.datasource.remote.model.CreditDetailResponse
import nekit.corporation.loan_shared.data.datasource.remote.model.CreditResponse
import nekit.corporation.loan_shared.data.datasource.remote.model.IssueCreditRequest
import nekit.corporation.loan_shared.data.datasource.remote.model.OverdueDto
import nekit.corporation.loan_shared.data.datasource.remote.model.RatingDto
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

    @GET("credit/credits/overdue/me")
    suspend fun getOverdueCredits(): List<OverdueDto>

    @POST("credit/credits/{id}/repay")
    suspend fun repayCredit(
        @Path("id") id: Int
    ): CreditResponse

    @GET("credit/credits/rating/me")
    suspend fun getRating(): RatingDto

}