package nekit.corporation.presentation.state

import android.content.Context
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.navigation.LoanStateNavigation
import nekit.corporation.presentation.model.LoanStateState
import nekit.corporation.utils.formatDateDaysFromNow
import java.util.Locale
import javax.inject.Inject

class LoanStateViewModel @Inject constructor(
    private val loanStateNavigation: LoanStateNavigation
) : StatefulViewModel<LoanStateState>() {

    override fun createInitialState(): LoanStateState {
        return LoanStateState.Init
    }

    fun init(amount: Int?, context: Context, period: Int?) {
        updateState {
            if (amount == null || period == null) {
                LoanStateState.Rejected
            } else {
                LoanStateState.Approved(
                    amount,
                    formatDateDaysFromNow(
                        period.toLong(),
                        Locale(LocaleManager.getPersistedLanguage(context))
                    )
                )
            }
        }
    }

    fun onButtonClick() {
        when (screenState.value.currentState) {
            is LoanStateState.Approved -> loanStateNavigation.onBanksOpen()
            LoanStateState.Init -> Unit
            LoanStateState.Rejected -> loanStateNavigation.onClose()
        }
    }

    fun onBackClick() {
        loanStateNavigation.onClose()
    }
}
