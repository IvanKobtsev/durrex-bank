package nekit.corporation.tariff.data.remote

import nekit.corporation.tariff.data.remote.model.CreateTariffRequest
import nekit.corporation.tariff.data.remote.model.TariffResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TariffApi {
   
    @GET("tariffs")
    suspend fun getTariffs(
    ): List<TariffResponse>

    @POST("tariffs")
    suspend fun createTariff(
        @Body request: CreateTariffRequest
    ): TariffResponse
}