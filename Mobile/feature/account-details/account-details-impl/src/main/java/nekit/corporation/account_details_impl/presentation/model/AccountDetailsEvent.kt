package nekit.corporation.presentation.model

import androidx.annotation.StringRes
import nekit.corporation.architecture.presentation.Event

sealed interface AccountDetailsEvent: Event {

    data class ShowToast(@param:StringRes val textRes: Int) : AccountDetailsEvent
}
