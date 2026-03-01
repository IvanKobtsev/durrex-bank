package nekit.corporation.presentation.model

import androidx.annotation.StringRes
import nekit.corporation.architecture.presentation.Event

sealed interface LoanDetailsEvent: Event {

    data class ShowToast(@StringRes val textRes: Int) : LoanDetailsEvent
}
