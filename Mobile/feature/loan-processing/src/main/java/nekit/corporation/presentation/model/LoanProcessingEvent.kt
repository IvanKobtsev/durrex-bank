package nekit.corporation.presentation.model

import androidx.annotation.StringRes
import nekit.corporation.architecture.presentation.Event

sealed interface LoanProcessingEvent : Event {

    data class ShowToast(@StringRes val textRes: Int) : LoanProcessingEvent
}