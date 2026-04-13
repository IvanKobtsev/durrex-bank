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
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.loan_shared.data.datasource.remote.model.AccountStatus
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
        when (throwable) {
            is ForbiddenFailure -> {
                mainNavigation.openAuth()
            }

            else -> {
                screenEvents.offerEvent(MainEvent.ShowToast(R.string.strange_error))
            }
        }
        screenEvents.offerEvent(MainEvent.ShowToast(R.string.strange_error))
    }
    private var mainTask: Job? = null

    fun init() {
        mainTask = viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            supervisorScope {
                val accounts = async { accountRepository.getAllAccounts() }
                val credits = async { getCreditsUseCase() }
                val settings = async { getSettingsUseCase() }
                launch {
                    val credits = credits.await()
                    updateState {
                        with(
                            when (this) {
                                is MainState.Content -> this
                                MainState.Loading -> MainState.Content.default
                            }
                        ) {
                            copy(
                                credits = credits.take(3).toImmutableList()
                            )
                        }
                    }
                }
                launch {
                    val accounts = accounts.await()
                    val settings = settings.await()
                    val allAccountsList = accounts.toImmutableList()
                    val hiddenIds = settings.hiddenAccountIds.toImmutableList()
                    val filteredAccounts =
                        allAccountsList.filter { !hiddenIds.contains(it.id) && it.status == AccountStatus.Open }
                            .take(10).toImmutableList()
                    screenEvents.offerEvent(MainEvent.UpdateTheme(settings.theme == Scheme.dark))
                    updateState {
                        with(
                            when (this) {
                                is MainState.Content -> this
                                MainState.Loading -> MainState.Content.default
                            }
                        ) {
                            copy(
                                allAccounts = accounts.toImmutableList(),
                                accounts = filteredAccounts,
                                hidden = settings.hiddenAccountIds.toImmutableList()
                            )
                        }
                    }
                }
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
            updateStateOf<MainState.Content> {
                copy(isLoading = true)
            }
            viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
                supervisorScope {
                    launch {
                        accountRepository.createAccount(
                            CreateAccount(state.selectedCurrency.name)
                        )
                    }.join()
                    launch {
                        accountRepository.getAllAccounts().let {
                            updateStateOf<MainState.Content> {
                                copy(
                                    allAccounts = it.toImmutableList(),
                                    accounts = it.filter { account ->
                                        (if (showHidden)
                                            hidden.contains(account.id)
                                        else
                                            !hidden.contains(account.id)
                                                ) && account.status == AccountStatus.Open
                                    }.take(10).toImmutableList()
                                )
                            }
                        }
                    }.join()

                    launch {
                        updateStateOf<MainState.Content> {
                            copy(isLoading = false)
                        }
                    }.join()
                }
            }
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
            copy(
                showHidden = !showHidden,
                accounts = allAccounts.filter {
                    (if (showHidden)
                        !hidden.contains(it.id)
                    else
                        hidden.contains(it.id)) && it.status == AccountStatus.Open
                }.toImmutableList(),
            )
        }
    }

    override fun onCreateTransactionClick() {
        mainNavigation.openCreateTransaction()
    }

    override fun onDismissCurrency() {
        updateStateOf<MainState.Content> {
            copy(
                isCurrencyMenuOpen = false,
            )
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
