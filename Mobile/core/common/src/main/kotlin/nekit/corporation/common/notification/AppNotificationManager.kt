package nekit.corporation.common.notification

import android.app.Notification

interface AppNotificationManager {

    fun show(notificationId: Int, notification: Notification)

    fun cancel(notificationId: Int)
}