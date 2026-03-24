package nekit.corporation.user.data.remote

import nekit.corporation.user.data.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {

    @GET("user/users/me")
    suspend fun getUser(): UserResponse

    @GET("user/users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserResponse
}