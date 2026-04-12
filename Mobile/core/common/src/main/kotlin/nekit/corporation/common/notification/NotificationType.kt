package nekit.corporation.common.notification

sealed class NotificationType {

    data class Simple(
        val title: String,
        val body: String,
        val icon: String?,
    ) : NotificationType()

    data class Actionable(
        val title: String,
        val body: String,
        val positiveAction: NotificationAction,
        val negativeAction: NotificationAction
    ) : NotificationType()
}

data class NotificationAction(
    val label: String,
    val deepLink: String,
    val requestCode: Int
)