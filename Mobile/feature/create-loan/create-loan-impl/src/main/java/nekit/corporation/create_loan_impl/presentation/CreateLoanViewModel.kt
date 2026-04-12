package nekit.corporation.create_loan_impl.presentation

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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.create_loan_impl.R
import nekit.corporation.create_loan_impl.model.AccountUi
import nekit.corporation.create_loan_impl.model.CreateLoanEvents
import nekit.corporation.create_loan_impl.model.CreateLoanInteractions
import nekit.corporation.create_loan_impl.model.CreateLoanState
import nekit.corporation.create_loan_impl.model.toUi
import nekit.corporation.create_loan_impl.navigation.CreateCreditNavigator
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.loan_shared.domain.usecase.CreateCreditUseCase
import nekit.corporation.tariff.domain.usecase.GetTariffsUseCase

@Inject
@ViewModelKey(CreateLoanViewModel::class)
@ContributesIntoMap(
    AppScope::class, binding = binding<ViewModel>()
)
class CreateLoanViewModel(
    private val navigation: CreateCreditNavigator,
    private val createCreditUseCase: CreateCreditUseCase,
    private val getTariffsUseCase: GetTariffsUseCase,
    private val accountRepository: AccountRepository,
) : StatefulViewModel<CreateLoanState>(), CreateLoanInteractions {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleUnknownError()
        Log.d(TAG, throwable.message.toString())
    }

    init {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            supervisorScope {
                val accounts = async { accountRepository.getAllAccounts() }
                val tariffs = async { getTariffsUseCase() }
                runCatching { accounts.await() to tariffs.await() }.onSuccess { (accounts, tariffs) ->
                    updateState {
                        copy(
                            selectedAccount = accounts.firstOrNull()?.toUi(),
                            selectedTariff = tariffs.firstOrNull(),
                            accounts = accounts.map { it.toUi() }.toImmutableList(),
                            tariffs = tariffs.toImmutableList(),
                            isLoading = false
                        )
                    }
                }.onFailure {
                    updateState {
                        copy(
                            isFatalError = true,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    override fun createInitialState(): CreateLoanState {
        return CreateLoanState.default
    }

    override fun onBackClick() {
        navigation.onBack()
    }

    override fun onSelectTariff(tariff: String) {
        updateState {
            copy(selectedTariff = tariffs.first { it.name == tariff })
        }
    }

    override fun onExpandedTariffChange(isOpen: Boolean) {
        updateState {
            copy(isTariffOpen = isOpen)
        }
    }

    override fun onExpandedAccountChange(isOpen: Boolean) {
        updateState {
            copy(isAccountOpen = isOpen)
        }
    }


    override fun onSelectAccount(account: AccountUi) {
        updateState {
            copy(selectedAccount = account)
        }
    }

    override fun onCreateCredit() {
        viewModelScope.launch(Dispatchers.IO) {
            with(currentScreenState) {
                try {
                    if (selectedAccount != null && selectedTariff != null) {
                        createCreditUseCase(
                            accountId = selectedAccount.id,
                            tariffId = selectedTariff.id,
                            amount = amount
                        )
                    }
                } catch (e: Throwable) {
                    screenEvents.offerEvent(CreateLoanEvents.ShowToast(R.string.error))
                    Log.d(TAG, "error: ${e.message}")
                }
                navigation.onBack()
            }
        }

    }

    override fun onChangeAmount(amount: String) {
        updateState {
            copy(amount = amount.toDouble())
        }
    }

    private fun handleUnknownError() {
        offerEvent(CreateLoanEvents.ShowToast(R.string.error))
    }

    companion object {
        private const val TAG = "CreateLoanViewModel"
    }
}