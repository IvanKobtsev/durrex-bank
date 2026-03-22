package nekit.corporation.transaction_details_impl.presentation.model

import androidx.annotation.StringRes
import nekit.corporation.architecture.presentation.Event

sealed interface TransactionDetailsEvent: Event {

    data class ShowToast(@param:StringRes val textRes: Int) : TransactionDetailsEvent
}
