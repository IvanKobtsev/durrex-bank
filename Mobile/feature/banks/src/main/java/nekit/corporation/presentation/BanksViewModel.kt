package nekit.corporation.presentation

import nekit.corporation.architecture.presentation.EmptyState
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.navigation.BankNavigation
import javax.inject.Inject

class BanksViewModel @Inject constructor(
    private val bankNavigation: BankNavigation
) : StatefulViewModel<EmptyState>() {

    override fun createInitialState(): EmptyState {
        return EmptyState
    }

    fun onClose() {
        bankNavigation.onClose()
    }

    fun onMainOpen() {
        bankNavigation.onMainOpen()
    }
}
