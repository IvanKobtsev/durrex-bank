package nekit.corporation.user.data.remote

import nekit.corporation.user.data.model.PagedResponseOfTransactionResponse
import nekit.corporation.user.data.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("accounts")
    suspend fun getUser(): UserResponse

    @GET("api/accounts/{id}/transactions")
    suspend fun getTransactions(
        @Path("id") id: Int,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 200
    ): PagedResponseOfTransactionResponse
}