package nekit.corporation.main_impl.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.loan_shared.data.datasource.remote.model.AccountStatus
import nekit.corporation.loan_shared.domain.model.Account
import nekit.corporation.loan_shared.domain.model.CreateAccount
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.loan_shared.domain.usecase.GetCreditsUseCase
import nekit.corporation.main_impl.R
import nekit.corporation.main_impl.navigation.MainNavigation
import nekit.corporation.main_impl.presentation.models.Currency
import nekit.corporation.main_impl.presentation.models.MainEvent
import nekit.corporation.main_impl.presentation.models.MainState
import nekit.corporation.main_impl.presentation.models.MainViewModelInteraction
import nekit.corporation.user.domain.model.Scheme
import nekit.corporation.user.domain.usecase.GetSettingsUseCase
import nekit.corporation.user.domain.usecase.SaveSettingsUseCase
import nekit.corporation.util.domain.common.CircuitBreakerOpenFailure
import nekit.corporation.util.domain.common.ForbiddenFailure

@Inject
@ViewModelKey(MainViewModel::class)
@ContributesIntoMap(
    AppScope::class, binding = binding<ViewModel>()
)
class MainViewModel(
    private val mainNavigation: MainNavigation,
    private val accountRepository: AccountRepository,
    private val getCreditsUseCase: GetCreditsUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
) : StatefulViewModel<MainState>(), MainViewModelInteraction {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d(TAG, "throwable: $throwable")

        when (throwable) {
            is ForbiddenFailure -> mainNavigation.openAuth()
            is CircuitBreakerOpenFailure -> Log.d(TAG, "break: $throwable")
            else -> screenEvents.offerEvent(MainEvent.ShowToast(R.string.strange_error))
        }
        updateState {
            if (this is MainState.Content) this.copy(isLoading = false)
            else MainState.Content.default.copy(isLoading = false)
        }
    }

    private var mainTask: Job? = null

    override fun createInitialState(): MainState = MainState.Loading

    fun init() {
        mainTask?.cancel()
        mainTask = viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            try {
                Log.d(TAG,"start update")
                loadMainData()
            } finally {
                Log.d(TAG,"end update")
                updateStateOf<MainState.Content> {
                    copy(isLoading = false)
                }
            }
        }
    }

    private suspend fun loadMainData() = supervisorScope {
        val accountsDeferred = async { accountRepository.getAllAccounts() }
        val creditsDeferred = async { getCreditsUseCase() }
        val settingsDeferred = async { getSettingsUseCase() }

        val accounts = accountsDeferred.await()
        val credits = creditsDeferred.await()
        val settings = settingsDeferred.await()

        val hiddenIds = settings.hiddenAccountIds.toImmutableList()
        val filteredAccounts = filterAccounts(
            allAccounts = accounts,
            hiddenIds = hiddenIds,
            showHidden = false
        )

        screenEvents.offerEvent(MainEvent.UpdateTheme(settings.theme == Scheme.dark))

        updateState {
            MainState.Content.default.copy(
                credits = credits.take(3).toImmutableList(),
                allAccounts = accounts.toImmutableList(),
                accounts = filteredAccounts,
                hidden = hiddenIds,
                showHidden = false,
                isLoading = false
            )
        }
    }

    private fun filterAccounts(
        allAccounts: List<Account>,
        hiddenIds: List<Int>,
        showHidden: Boolean
    ) = allAccounts.filter { account ->

        (if (showHidden) {
            hiddenIds.contains(account.id)
        } else {
            !hiddenIds.contains(account.id)
        }) && account.status == AccountStatus.Open
    }.take(10).toImmutableList()

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
        val state = screenState.value.currentState as? MainState.Content ?: return

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            updateStateOf<MainState.Content> {
                copy(isLoading = true)
            }

            try {
                accountRepository.createAccount(
                    CreateAccount(state.selectedCurrency.name)
                )

                refreshAccounts()
            } finally {
                updateStateOf<MainState.Content> {
                    copy(isLoading = false)
                }
            }
        }
    }

    private suspend fun refreshAccounts() {
        val currentState = screenState.value.currentState as? MainState.Content ?: return
        val accounts = accountRepository.getAllAccounts()

        val filteredAccounts = filterAccounts(
            allAccounts = accounts,
            hiddenIds = currentState.hidden,
            showHidden = currentState.showHidden
        )

        updateStateOf<MainState.Content> {
            copy(
                allAccounts = accounts.toImmutableList(),
                accounts = filteredAccounts
            )
        }
    }

    override fun onShowAllLoansClick() {
        mainNavigation.openAllLoans()
    }

    override fun onShowAllAccountsClick() {
        mainNavigation.openAllAccounts()
    }

    override fun onLoanClick(id: Int) {
        mainNavigation.openLoanById(id)
    }

    override fun onAccountClick(id: Int) {
        mainNavigation.openAccountById(id)
    }

    override fun onHiddenSwitch() {
        updateStateOf<MainState.Content> {
            val newShowHidden = !showHidden
            copy(
                showHidden = newShowHidden,
                accounts = filterAccounts(
                    allAccounts = allAccounts,
                    hiddenIds = hidden,
                    showHidden = newShowHidden
                )
            )
        }
    }

    override fun onCreateTransactionClick() {
        mainNavigation.openCreateTransaction()
    }

    override fun onDismissCurrency() {
        updateStateOf<MainState.Content> {
            copy(isCurrencyMenuOpen = false)
        }
    }

    override fun onSelectCurrency(currency: Currency) {
        updateStateOf<MainState.Content> {
            copy(
                selectedCurrency = currency,
                isCurrencyMenuOpen = false
            )
        }
    }

    override fun onOpenCurrencyMenu() {
        updateStateOf<MainState.Content> {
            copy(isCurrencyMenuOpen = true)
        }
    }

    fun onNavigate() {
        mainTask?.cancel()
    }

    private companion object {
        private const val TAG = "MainViewModel"
    }
}