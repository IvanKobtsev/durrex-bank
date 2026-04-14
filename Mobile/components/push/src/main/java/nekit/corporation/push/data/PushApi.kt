package nekit.corporation.push.data

import retrofit2.http.Body
import retrofit2.http.POST

interface PushApi {

    @POST("core/api/push-notifications/subscribe")
    suspend fun sendPushToken(@Body token: FireBaseTokenDto)
}