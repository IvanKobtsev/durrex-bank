package nekit.corporation.transaction

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.loan_shared.domain.model.Transfer
import nekit.corporation.loan_shared.domain.usecase.CreateTransferUseCase
import nekit.corporation.loan_shared.domain.usecase.GetAccountByIdUseCase
import nekit.corporation.loan_shared.domain.usecase.GetAccountsUseCase
import nekit.corporation.transaction.model.TransactionInteractions
import nekit.corporation.transaction.model.TransactionState
import nekit.corporation.transaction.model.toUi
import nekit.corporation.user.domain.usecase.GetUserByIdUseCase
import nekit.corporation.user.domain.usecase.GetUserUseCase
import nekit.corporation.util.domain.common.NotFoundFailure

@OptIn(FlowPreview::class)
class TransactionViewModel(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val createTransferUseCase: CreateTransferUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getUserUseCase: GetUserUseCase,
) : StatefulViewModel<TransactionState>(), TransactionInteractions {

    override fun createInitialState(): TransactionState {
        return TransactionState.DEFAULT
    }

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            val accounts = getAccountsUseCase()
            val user = getUserUseCase()
            updateState {
                copy(
                    userAccounts = accounts.map { it.toUi() }.toImmutableList(),
                    accountFrom = accounts.firstOrNull()?.toUi(),
                    user = user.toUi()
                )
            }
        }
    }

    init {
        viewModelScope.launch(Dispatchers.Default) {
            screenState.distinctUntilChanged { old, new ->
                old.currentState.accountTo == new.currentState.accountTo
            }.debounce(400L).collect {
                try {
                    val account = getAccountByIdUseCase(it.currentState.accountTo.toInt())
                    val user = getUserByIdUseCase(account.ownerId)
                    updateState {
                        copy(
                            recipient = user.toUi()
                        )
                    }
                } catch (e: NotFoundFailure) {
                    updateState {
                        copy(
                            accountToError = R.string.account_not_found_exception
                        )
                    }
                }
            }
        }
    }

    override fun onAccountToChange(id: String) {
        updateState {
            copy(
                accountTo = id
            )
        }
    }

    override fun onAccountFromChoose(id: Int) {
        updateState {
            copy(
                accountFrom = userAccounts.first { it.id == id }
            )
        }
    }

    override fun onSumChange(sum: Double) {
        updateState {
            copy(
                sum = sum
            )
        }
    }

    override fun onTransferClick() {
        viewModelScope.launch(Dispatchers.IO) {
            val state = currentScreenState
            if (state.recipient != null && state.user != null) {
                createTransferUseCase(
                    transfer = Transfer(
                        targetAccountId = state.recipient.id,
                        amount = state.sum,
                        description = state.description
                    ),
                    id = state.user.id
                )
            }
        }
    }

    override fun descriptionChange(text: String) {
        updateState {
            copy(
                description = text
            )
        }
    }
}