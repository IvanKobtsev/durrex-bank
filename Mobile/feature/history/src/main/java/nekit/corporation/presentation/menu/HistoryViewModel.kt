package nekit.corporation.presentation.menu

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.navigation.MenuNavigation
import nekit.corporation.presentation.models.MenuState
import javax.inject.Inject
import kotlin.collections.flatten

class HistoryViewModel @Inject constructor(
    private val navigation: MenuNavigation,
    private val accountRepository: AccountRepository
) : StatefulViewModel<MenuState>() {

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val transactions = accountRepository.getAllAccounts()
                    .map { account ->
                        async {
                            runCatching { accountRepository.getTransactions(account.id) }
                                .getOrDefault(emptyList())
                        }
                    }.awaitAll()
                    .flatten()
                updateState {
                    copy(transactions = transactions.toImmutableList())
                }
            } catch (_: Throwable) {

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