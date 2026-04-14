package nekit.corporation.push.domain.repository

interface PushRepository {

    suspend fun sendPushToken(): Result<Unit>
}