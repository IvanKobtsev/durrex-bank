package nekit.corporation.auth.data.datasource.remote

import nekit.corporation.auth.data.datasource.remote.model.AuthDto
import nekit.corporation.auth.data.datasource.remote.model.RegisterDto
import nekit.corporation.auth.domain.model.TokenDto
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("/login")
    suspend fun login(@Body authData: AuthDto): TokenDto

    @POST("/registration")
    suspend fun register(@Body authData: RegisterDto): ResponseBody
}
