package nekit.corporation.main_impl.presentation.models

import androidx.annotation.StringRes
import nekit.corporation.architecture.presentation.Event

internal sealed interface MainEvent : Event {

    data class ShowToast(@param:StringRes val textRes: Int) : MainEvent

    data class UpdateTheme(val isDarkTheme: Boolean) : MainEvent
}