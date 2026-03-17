package nekit.corporation.transaction_details.presentation.model

import androidx.annotation.StringRes
import nekit.corporation.architecture.presentation.Event

sealed interface TransactionDetailsEvent: Event {

    data class ShowToast(@StringRes val textRes: Int) : TransactionDetailsEvent
}
