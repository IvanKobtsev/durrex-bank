package nekit.corporation.push.data

import com.google.firebase.messaging.FirebaseMessaging
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.tasks.await
import nekit.corporation.push.domain.repository.PushRepository

@Inject
@ContributesBinding(AppScope::class)
class PushRepositoryImpl(
    private val pushApi: PushApi
) : PushRepository {

    override suspend fun sendPushToken(): Result<Unit> {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            pushApi.sendPushToken(FireBaseTokenDto(fireBaseToken = token))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}