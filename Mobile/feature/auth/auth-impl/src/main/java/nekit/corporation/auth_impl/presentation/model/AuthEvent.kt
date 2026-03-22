package nekit.corporation.auth_impl.presentation.model

import android.content.Intent
import androidx.annotation.StringRes
import nekit.corporation.architecture.presentation.Event

sealed interface AuthEvent : Event {

    data class ShowToast(@param:StringRes val stringResId: Int) : AuthEvent

    class ChangeTheme(val isDark: Boolean) : AuthEvent

    data class OpenLogin(val intent: Intent) : AuthEvent
}
