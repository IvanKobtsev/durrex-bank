package nekit.corporation.transaction_impl.model

import nekit.corporation.architecture.presentation.Event

sealed interface TransactionEvents: Event {

    class ShowToast(val textRes: Int): TransactionEvents
}

