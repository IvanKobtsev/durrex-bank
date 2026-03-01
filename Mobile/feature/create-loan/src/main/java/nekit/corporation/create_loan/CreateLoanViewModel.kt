package nekit.corporation.create_loan

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.create_loan.model.CreateLoanInteractions
import nekit.corporation.create_loan.model.CreateLoanState
import nekit.corporation.create_loan.model.toUi
import nekit.corporation.create_loan.navigation.CreateCreditNavigation
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.loan_shared.domain.usecase.CreateCreditUseCase
import nekit.corporation.tariff.domain.usecase.GetTariffsUseCase
import nekit.corporation.user.domain.GetUserUseCase
import javax.inject.Inject
import kotlin.properties.Delegates

class CreateLoanViewModel @Inject constructor(
    private val navigation: CreateCreditNavigation,
    private val createCreditUseCase: CreateCreditUseCase,
    private val getTariffsUseCase: GetTariffsUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val accountRepository: AccountRepository,
) : StatefulViewModel<CreateLoanState>(), CreateLoanInteractions {
    private var userId by Delegates.notNull<Int>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val user = getUserUseCase()

            val accounts = async { accountRepository.getAllAccounts(user.id) }
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

    override fun onSelectAccount(account: String) {
        updateState {
            copy(selectedAccount = accounts.firstOrNull { it.id == extractIdFromString(account) })
        }
    }

    override fun onCreateCredit() {
        viewModelScope.launch(Dispatchers.IO) {
            with(currentScreenState) {
                if (selectedAccount != null && selectedTariff != null)
                    createCreditUseCase(
                        accountId = selectedAccount.id,
                        tariffId = selectedTariff.id,
                        amount = amount
                    )
            }
        }

    }

    override fun onChangeAmount(amount: String) {
        updateState {
            copy(amount = amount.toDouble())
        }
    }

    private fun extractIdFromString(input: String): Int? {
        return input.substringAfter("id: ")
            .substringBefore("\n")
            .toIntOrNull()
    }
}