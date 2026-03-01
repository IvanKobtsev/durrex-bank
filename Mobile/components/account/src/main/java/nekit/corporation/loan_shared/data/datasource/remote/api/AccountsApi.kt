package nekit.corporation.loan_shared.data.datasource.remote.api

import nekit.corporation.loan_shared.data.datasource.remote.model.AccountResponse
import nekit.corporation.loan_shared.data.datasource.remote.model.CreateAccountCommand
import nekit.corporation.loan_shared.data.datasource.remote.model.DebitRequest
import nekit.corporation.loan_shared.data.datasource.remote.model.DepositRequest
import nekit.corporation.loan_shared.data.datasource.remote.model.PagedResponseOfTransactionResponse
import nekit.corporation.loan_shared.data.datasource.remote.model.TransactionResponse
import nekit.corporation.loan_shared.data.datasource.remote.model.TransferRequest
import nekit.corporation.loan_shared.data.datasource.remote.model.WithdrawRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AccountsApi {

    @POST("api/accounts")
    suspend fun createAccount(
        @Body request: CreateAccountCommand
    ): AccountResponse

    @GET("api/accounts")
    suspend fun getAccounts(
        @Query("ownerId") ownerId: Int? = null
    ): List<AccountResponse>

    @GET("api/accounts/{id}")
    suspend fun getAccount(
        @Path("id") id: Int
    ): AccountResponse

    @DELETE("api/accounts/{id}")
    suspend fun deleteAccount(
        @Path("id") id: Int
    ): AccountResponse

    @POST("api/accounts/{id}/deposit")
    suspend fun deposit(
        @Path("id") id: Int,
        @Body request: DepositRequest
    ): TransactionResponse

    @POST("api/accounts/{id}/withdraw")
    suspend fun withdraw(
        @Path("id") id: Int,
        @Body request: WithdrawRequest
    ): TransactionResponse

    @POST("api/accounts/{id}/transfer")
    suspend fun transfer(
        @Path("id") id: Int,
        @Body request: TransferRequest
    ): TransactionResponse

    @POST("api/accounts/{id}/debit")
    suspend fun debit(
        @Path("id") id: Int,
        @Body request: DebitRequest
    ): TransactionResponse

    @GET("api/accounts/{id}/transactions")
    suspend fun getTransactions(
        @Path("id") id: Int,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): PagedResponseOfTransactionResponse
}