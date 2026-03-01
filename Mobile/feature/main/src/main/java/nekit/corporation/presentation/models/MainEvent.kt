package nekit.corporation.presentation.models

import androidx.annotation.StringRes
import nekit.corporation.architecture.presentation.Event

sealed interface MainEvent : Event {

    data class ShowToast(@StringRes val textRes: Int) : MainEvent
}