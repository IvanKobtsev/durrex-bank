package nekit.corporation.push

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.android.MetroApplication
import nekit.corporation.common.notification.AppNotificationManager
import nekit.corporation.common.notification.NotificationAction
import nekit.corporation.common.notification.NotificationFactory
import nekit.corporation.common.notification.NotificationType

class PushService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationFactory: NotificationFactory

    @Inject
    lateinit var notificationManager: AppNotificationManager

    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.let {
            Log.d(TAG, "Message: ${it.body}")
        }
        val type = when {
            message.data.containsKey("action_positive") -> NotificationType.Actionable(
                title = message.notification?.title ?: "",
                body = message.notification?.body ?: "",
                positiveAction = NotificationAction(
                    label = message.data["action_positive"]!!,
                    deepLink = message.data["positive_deeplink"]!!,
                    requestCode = 1
                ),
                negativeAction = NotificationAction(
                    label = message.data["action_negative"]!!,
                    deepLink = message.data["negative_deeplink"]!!,
                    requestCode = 2
                )
            )

            else -> {
                NotificationType.Simple(
                    title = message.notification?.title
                        ?: this.baseContext.getString(R.string.notification),
                    body = message.notification?.body ?: "",
                    icon = message.notification?.icon
                )
            }
        }
        val notification = notificationFactory.create(type, getChannelId(type))
        notificationManager.show(System.currentTimeMillis().toInt(), notification)
        message.notification?.let {
            Log.d(TAG, "Message: ${it.body}")
        }
    }

    private fun getChannelId(type: NotificationType): String {
        return when (type) {
            is NotificationType.Simple -> "default_channel"
            is NotificationType.Actionable -> "actionable_channel"
        }
    }

    override fun onCreate() {
        super.onCreate()
        val appGraph =
            (application as MetroApplication).appComponentProviders as PushSubGraph.Factory
        appGraph.create().inject(this)
    }

    override fun onNewToken(token: String) {
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        Log.d(TAG, "token: $token")
    }

    private companion object {
        private const val TAG = "PushService"
    }
}
