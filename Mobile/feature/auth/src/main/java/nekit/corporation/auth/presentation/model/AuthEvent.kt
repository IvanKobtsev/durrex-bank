package nekit.corporation.auth.presentation.model

import androidx.annotation.StringRes
import nekit.corporation.architecture.presentation.Event

sealed interface AuthEvent : Event {

    data class ShowToast(@StringRes val stringResId: Int) : AuthEvent
}
