package nekit.corporation.common.notification.impl

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import nekit.corporation.common.R
import nekit.corporation.common.notification.NotificationFactory
import nekit.corporation.common.notification.NotificationType

@Inject
@ContributesBinding(AppScope::class)
class AndroidNotificationFactory(
    private val context: Context,
) : NotificationFactory {

    override fun create(type: NotificationType, channelId: String): Notification {
        return when (type) {
            is NotificationType.Simple -> buildSimple(type, channelId)
            is NotificationType.Actionable -> buildActionable(type, channelId)
        }
    }

    private fun buildSimple(type: NotificationType.Simple, channelId: String): Notification {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notifications_ic)
            .setContentTitle(type.title)
            .setContentText(type.body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
    }

    private fun buildActionable(
        type: NotificationType.Actionable,
        channelId: String
    ): Notification {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notifications_ic)
            .setContentTitle(type.title)
            .setContentText(type.body)
            .setAutoCancel(true)
            .build()
    }
}