package nekit.corporation.profile.mvvm

import android.content.Intent
import androidx.annotation.StringRes
import nekit.corporation.architecture.presentation.Event

sealed interface UiEvents : Event {

    class ShowToast(@param:StringRes val textRes: Int) : UiEvents

    object ChangeTheme : UiEvents

    class OnLogout(val intent: Intent) : UiEvents
}