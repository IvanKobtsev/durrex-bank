package nekit.corporation.user.data.remote

import nekit.corporation.user.data.model.SettingsDto
import retrofit2.http.GET
import retrofit2.http.POST

interface SettingsApi {

    @GET("/settings")
    suspend fun getSettings(): SettingsDto

    @POST("/settings")
    suspend fun setSettings(settingsDto: SettingsDto)
}