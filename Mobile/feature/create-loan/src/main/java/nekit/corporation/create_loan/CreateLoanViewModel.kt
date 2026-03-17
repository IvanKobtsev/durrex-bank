package nekit.corporation.create_loan

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.create_loan.model.AccountUi
import nekit.corporation.create_loan.model.CreateLoanInteractions
import nekit.corporation.create_loan.model.CreateLoanState
import nekit.corporation.create_loan.model.toUi
import nekit.corporation.create_loan.navigation.CreateCreditNavigation
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.loan_shared.domain.usecase.CreateCreditUseCase
import nekit.corporation.tariff.domain.usecase.GetTariffsUseCase
import javax.inject.Inject

class CreateLoanViewModel @Inject constructor(
    private val navigation: CreateCreditNavigation,
    private val createCreditUseCase: CreateCreditUseCase,
    private val getTariffsUseCase: GetTariffsUseCase,
    private val accountRepository: AccountRepository,
) : StatefulViewModel<CreateLoanState>(), CreateLoanInteractions {
    init {
        viewModelScope.launch(Dispatchers.IO) {
            val accounts = async {
                accountRepository.getAllAccounts()
            }
            val tariffs = async { getTariffsUseCase() }
            updateState {
                copy(
                    selectedAccount = accounts.await().firstOrNull()?.toUi(),
                    selectedTariff = tariffs.await().firstOrNull(),
                    accounts = accounts.await().map { it.toUi() }.toImmutableList(),
                    tariffs = tariffs.await().toImmutableList(),
                )
            }
        }
    }

    override fun createInitialState(): CreateLoanState {
        return CreateLoanState(
            selectedAccount = null,
            selectedTariff = null,
            tariffs = persistentListOf(),
            isTariffOpen = false,
            isAccountOpen = false,
            accounts = persistentListOf(),
            amount = 0.0,
            isLoading = true
        )
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
                if (selectedAccount != null && selectedTariff != null) {
                    createCreditUseCase(
                        accountId = selectedAccount.id,
                        tariffId = selectedTariff.id,
                        amount = amount
                    )
                    navigation.onBack()
                }
            }
        }

    }

    override fun onChangeAmount(amount: String) {
        updateState {
            copy(amount = amount.toDouble())
        }
    }
}