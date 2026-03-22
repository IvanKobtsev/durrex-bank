package nekit.corporation.main_impl.presentation

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
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.loan_shared.domain.model.CreateAccount
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.loan_shared.domain.usecase.GetCreditsUseCase
import nekit.corporation.main_impl.R
import nekit.corporation.main_impl.navigation.MainNavigation
import nekit.corporation.main_impl.presentation.models.Currency
import nekit.corporation.main_impl.presentation.models.MainEvent
import nekit.corporation.main_impl.presentation.models.MainState
import nekit.corporation.main_impl.presentation.models.MainViewModelInteraction
import nekit.corporation.user.domain.model.Settings
import nekit.corporation.user.domain.usecase.GetSettingsUseCase
import nekit.corporation.user.domain.usecase.SaveSettingsUseCase
import nekit.corporation.util.domain.common.ForbiddenFailure
import nekit.corporation.util.domain.common.NotFoundFailure

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
    private val saveSettingsUseCase: SaveSettingsUseCase,
) : StatefulViewModel<MainState>(), MainViewModelInteraction {
    private val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        screenEvents.offerEvent(MainEvent.ShowToast(R.string.strange_error))
        Log.d(TAG, "throwable: $throwable")
    }
    val mainTask: Job

    init {
        mainTask = viewModelScope.launch(Dispatchers.IO + exceptionHandler + SupervisorJob()) {
            val accounts = async {
                load { accountRepository.getAllAccounts() }
            }
            val credits = async {
                load { getCreditsUseCase() }
            }
            val settings = async {
                fallback(
                    action = { getSettingsUseCase() },
                    onFailure = {
                        when (it) {
                            is ForbiddenFailure -> {
                                mainNavigation.openAuth()
                            }

                            is NotFoundFailure -> {
                                load {
                                    saveSettingsUseCase(Settings.default)
                                }
                            }

                            else -> {
                                Log.d("RAG", "${it.message}")
                                screenEvents.offerEvent(MainEvent.ShowToast(R.string.strange_error))
                            }
                        }
                    }
                )
            }
            launch {
                updateState {
                    initContent.copy(
                        allAccounts = accounts.await()?.toImmutableList()
                            ?: persistentListOf(),
                        accounts = accounts.await()?.filter {
                            val s = settings.await()
                            s != null && !s.hiddenAccountIds.contains(it.id)
                        }?.toImmutableList() ?: persistentListOf(),
                        hidden = settings.await()?.hiddenAccountIds?.toImmutableList()
                            ?: persistentListOf()
                    )
                }
            }
            launch {
                updateState {
                    initContent.copy(
                        credits = credits.await()?.toImmutableList() ?: persistentListOf()
                    )
                }
            }
        }
    }

    private suspend fun <T> load(action: suspend () -> T): T? {
        return fallback(
            action = { action() },
            onFailure = {
                if (it is ForbiddenFailure) {
                    mainNavigation.openAuth()
                } else {
                    screenEvents.offerEvent(MainEvent.ShowToast(R.string.strange_error))
                }
            }
        )
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
            viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
                accountRepository.createAccount(
                    CreateAccount(state.selectedCurrency.name)
                )
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
                    if (showHidden)
                        !hidden.contains(it.id)
                    else
                        hidden.contains(it.id)
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
        mainTask.cancel()
    }

    private companion object {
        private const val TAG = "MainViewModel"
        val initContent = MainState.Content(
            loanSum = 5_000,
            isCurrencyMenuOpen = false,
            selectedCurrency = Currency.RUB,
            credits = persistentListOf(),
            accounts = persistentListOf(),
            showHidden = false,
            hidden = persistentListOf(),
            allAccounts = persistentListOf(),
        )
    }
}
