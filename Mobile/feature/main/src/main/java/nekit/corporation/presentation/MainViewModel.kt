package nekit.corporation.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.domain.Currency
import nekit.corporation.loan_shared.domain.model.CreateAccount
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.loan_shared.domain.usecase.GetCreditsUseCase
import nekit.corporation.navigation.MainNavigation
import nekit.corporation.presentation.models.MainState
import nekit.corporation.presentation.models.MainViewModelInteraction
import nekit.corporation.user.domain.GetUserUseCase
import nekit.corporation.user.domain.model.User
import javax.inject.Inject


class MainViewModel @Inject constructor(
    private val mainNavigation: MainNavigation,
    private val accountRepository: AccountRepository,
    private val getCreditsUseCase: GetCreditsUseCase,
    private val getUserUseCase: GetUserUseCase,
) : StatefulViewModel<MainState>(), MainViewModelInteraction {
    private lateinit var user: User

    init {
        viewModelScope.launch(Dispatchers.IO) {
            user = getUserUseCase()
            val accounts = async { accountRepository.getAllAccounts(user.id) }
            val credits = async { getCreditsUseCase(user.id) }
            updateState {
                initContent.copy(
                    accounts = accounts.await().toImmutableList(),
                    credits = credits.await().toImmutableList()
                )
            }
        }
    }

    override fun createInitialState(): MainState {
        return MainState.Loading
    }

    override fun openOnboarding() {
        mainNavigation.openOnboarding()
    }

    override fun onCreateCreditClick() {
        val state = screenState.value.currentState

        if (state is MainState.Content) {
            mainNavigation.openCreateCredit()
        }
    }

    override fun onAccountCreateClick() {
        val state = screenState.value.currentState

        if (state is MainState.Content) {
            viewModelScope.launch(Dispatchers.IO) {
                accountRepository.createAccount(
                    CreateAccount(user.id, state.selectedCurrency.name)
                )
            }
        }
    }

    override fun onShowAllLoansClick() {
        mainNavigation.openAllLoans()
    }

    override fun onLoanClick(id: Int) {
        mainNavigation.openLoanById(id)
    }

    override fun onDismissCurrency() {
        updateStateOf<MainState.Content> {
            copy(isCurrencyMenuOpen = false)
        }
    }

    override fun onSelectCurrency(currency: Currency) {
        updateStateOf<MainState.Content> {
            copy(selectedCurrency = currency)
        }
    }

    override fun onOpenCurrencyMenu() {
        updateStateOf<MainState.Content> {
            copy(isCurrencyMenuOpen = true)
        }
    }

    private companion object {
        val initContent = MainState.Content(
            loanSum = 5_000,
            isCurrencyMenuOpen = false,
            selectedCurrency = Currency.RUB,
            credits = persistentListOf(),
            accounts = persistentListOf(),
        )
    }
}
