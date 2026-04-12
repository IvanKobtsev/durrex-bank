package nekit.corporation.common.notification

import android.app.Notification

interface NotificationFactory {

    fun create(
        type: NotificationType,
        channelId: String = DEFAULT_CHANNEL
    ): Notification

    private companion object {
        const val DEFAULT_CHANNEL = "DEFAULT_CHANNEL"
    }
}