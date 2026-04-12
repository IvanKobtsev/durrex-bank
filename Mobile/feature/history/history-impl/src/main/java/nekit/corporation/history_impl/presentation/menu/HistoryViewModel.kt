package nekit.corporation.history_impl.presentation.menu

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineExceptionHandler
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

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleUnknownError()
        Log.d(TAG, throwable.message.toString())
    }

    init {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            accountRepository.getAllAccounts()
                .map { account ->
                    async {
                        runCatching { accountRepository.getTransactions(account.id) }
                            .getOrDefault(emptyList())
                    }
                }
                .awaitAll()
                .flatten()
                .sortedBy { it.id }
                .let {
                    updateState { copy(transactions = it.toImmutableList()) }
                }
        }
    }

    fun onTransactionClick(transactionId: Long) {
        navigation.openDetails(
            currentScreenState.transactions.find { it.id == transactionId }!!.accountId,
            transactionId
        )
    }

    private fun handleUnknownError() {
        offerEvent(HistoryEvent.ShowToast(R.string.strange_error))
    }


    override fun createInitialState(): MenuState {
        return MenuState(
            transactions = persistentListOf(),
        )
    }

    fun openOnboarding() {
        navigation.openOnboarding()
    }

    companion object {
        private const val TAG = "HistoryViewModel"
    }
}