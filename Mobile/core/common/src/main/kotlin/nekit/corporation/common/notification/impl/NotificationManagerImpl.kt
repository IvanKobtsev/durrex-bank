package nekit.corporation.common.notification.impl

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import nekit.corporation.common.notification.AppNotificationManager

@Inject
@ContributesBinding(AppScope::class)
class NotificationManagerImpl(
    context: Context
) : AppNotificationManager {
    private val manager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun show(notificationId: Int, notification: Notification) {
        manager.notify(notificationId, notification)
    }

    override fun cancel(notificationId: Int) {
        manager.cancel(notificationId)
    }
}