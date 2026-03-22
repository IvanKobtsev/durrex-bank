package nekit.corporation.user.data.remote

import nekit.corporation.user.data.model.SettingsDto
import nekit.corporation.user.data.model.UpdateHiddenAccountsDto
import nekit.corporation.user.data.model.UpdateThemeDto
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SettingsApi {

    @GET("/mobile-app-settings/api/settings")
    suspend fun getSettings(): SettingsDto

    @PUT("/mobile-app-settings/api/settings")
    suspend fun setSettings(settingsDto: SettingsDto): SettingsDto

    @PATCH("/mobile-app-settings/api/settings/theme")
    suspend fun updateTheme(updateThemeDto: UpdateThemeDto): SettingsDto

    @PATCH("/mobile-app-settings/api/settings/hidden-accounts")
    suspend fun updateHiddenAccounts(updateHiddenAccountsDto: UpdateHiddenAccountsDto): SettingsDto
}

