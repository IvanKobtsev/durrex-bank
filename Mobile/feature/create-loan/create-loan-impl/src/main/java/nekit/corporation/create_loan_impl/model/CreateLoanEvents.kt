package nekit.corporation.create_loan_impl.model

import androidx.annotation.StringRes
import nekit.corporation.architecture.presentation.Event

sealed interface CreateLoanEvents : Event {

    class ShowToast(@param:StringRes val textRes: Int) : CreateLoanEvents

}