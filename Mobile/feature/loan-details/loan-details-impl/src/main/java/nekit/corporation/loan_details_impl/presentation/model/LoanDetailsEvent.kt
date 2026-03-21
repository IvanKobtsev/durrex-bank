package nekit.corporation.loan_details_impl.presentation.model

import androidx.annotation.StringRes
import nekit.corporation.architecture.presentation.Event

internal sealed interface LoanDetailsEvent: Event {

    data class ShowToast(@param:StringRes val textRes: Int) : LoanDetailsEvent
}
