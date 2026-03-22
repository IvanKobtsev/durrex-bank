package nekit.corporation.history_impl.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.history_impl.R
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.history_impl.navigation.MenuNavigator
import nekit.corporation.history_impl.presentation.menu.mvvm.HistoryEvent
import nekit.corporation.history_impl.presentation.models.MenuState
import nekit.corporation.util.domain.common.NoConnectionFailure
import kotlin.collections.flatten

@Inject
@ViewModelKey(HistoryViewModel::class)
@ContributesIntoMap(AppScope::class, binding = binding<ViewModel>())
class HistoryViewModel(
    private val navigation: MenuNavigator,
    private val accountRepository: AccountRepository
) : StatefulViewModel<MenuState>() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            accountRepository.getTransactionHubEvents().collect {
                fallback(
                    action = {
                        accountRepository.getAllAccounts()
                            .map { account ->
                                async {
                                    runCatching { accountRepository.getTransactions(account.id) }
                                        .getOrDefault(emptyList())
                                }
                            }.awaitAll()
                            .flatten()
                    },
                    onFailure = { error ->
                        when (error) {
                            is NoConnectionFailure -> offerEvent(HistoryEvent.ShowToast(R.string.network_error))
                            else -> offerEvent(HistoryEvent.ShowToast(R.string.strange_error))
                        }
                    }
                )?.let {
                    updateState {
                        copy(transactions = it.toImmutableList())
                    }
                }
                it.onFailure {
                    offerEvent(HistoryEvent.ShowToast(R.string.auth_error))
                    navigation.openAuth()
                }
            }
        }
    }

    fun onTransactionClick(transactionId: Long) {
        navigation.openDetails(
            currentScreenState.transactions.find { it.id == transactionId }!!.accountId,
            transactionId
        )
    }

    override fun createInitialState(): MenuState {
        return MenuState(
            transactions = persistentListOf(),
        )
    }

    fun openOnboarding() {
        navigation.openOnboarding()
    }
}