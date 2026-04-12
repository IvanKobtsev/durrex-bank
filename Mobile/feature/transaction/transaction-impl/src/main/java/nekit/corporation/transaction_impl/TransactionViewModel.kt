package nekit.corporation.transaction_impl

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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.loan_shared.domain.model.Transfer
import nekit.corporation.loan_shared.domain.usecase.CreateTransferUseCase
import nekit.corporation.loan_shared.domain.usecase.GetAccountByIdUseCase
import nekit.corporation.loan_shared.domain.usecase.GetAccountsUseCase
import nekit.corporation.transaction_impl.model.TransactionInteractions
import nekit.corporation.transaction.model.TransactionState
import nekit.corporation.transaction_impl.model.toUi
import nekit.corporation.transaction_impl.model.TransactionEvents
import nekit.corporation.user.domain.usecase.GetUserByIdUseCase
import nekit.corporation.user.domain.usecase.GetUserUseCase
import nekit.corporation.util.domain.common.NotFoundFailure

@OptIn(FlowPreview::class)
@Inject
@ViewModelKey(TransactionViewModel::class)
@ContributesIntoMap(AppScope::class, binding = binding<ViewModel>())
class TransactionViewModel(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val createTransferUseCase: CreateTransferUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val navigator: TransactionNavigator
) : StatefulViewModel<TransactionState>(), TransactionInteractions {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleUnknownError()
        Log.d(TAG, throwable.message.toString())
    }

    override fun createInitialState(): TransactionState {
        return TransactionState.DEFAULT
    }

    init {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val accounts = async { getAccountsUseCase() }
            val user = async { getUserUseCase() }
            updateState {
                copy(
                    userAccounts = accounts.await().map { it.toUi() }?.toImmutableList()
                        ?: persistentListOf(),
                    accountFrom = accounts.await().firstOrNull()?.toUi(),
                    user = user.await().toUi()
                )
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            screenState.distinctUntilChanged { old, new ->
                old.currentState.accountTo == new.currentState.accountTo
            }.debounce(400L).collect {
                try {
                    if (it.currentState.accountTo == "")
                        return@collect
                    val account = getAccountByIdUseCase(it.currentState.accountTo.toInt())
                    val user = getUserByIdUseCase(account.ownerId)
                    updateState {
                        copy(
                            recipient = user.toUi(),
                            accountToError = null
                        )
                    }
                } catch (_: NotFoundFailure) {
                    updateState {
                        copy(
                            accountToError = R.string.account_not_found_exception
                        )
                    }
                } catch (_: Throwable) {
                    screenEvents.offerEvent(TransactionEvents.ShowToast(R.string.strange_error))
                }
            }
        }

        viewModelScope.launch {
            screenState.distinctUntilChanged { old, new ->
                old.currentState == new.currentState
            }.collect {
                val state = it.currentState
                updateState {
                    copy(
                        isButtonEnable = state.recipient != null && state.user != null && state.accountToError == null && state.accountTo != ""
                    )
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
            if (state.recipient != null && state.accountFrom != null) {
                try {
                    updateState {
                        copy(isLoading = true, isButtonEnable = false)
                    }
                    createTransferUseCase(
                        transfer = Transfer(
                            targetAccountId = state.accountTo.toInt(),
                            amount = state.sum,
                            description = state.description
                        ),
                        id = state.accountFrom.id
                    )
                    screenEvents.offerEvent(TransactionEvents.ShowToast(R.string.transaction_success))
                } catch (_: Throwable) {
                    screenEvents.offerEvent(TransactionEvents.ShowToast(R.string.strange_error))
                    navigator.toMain()
                }
                updateState {
                    copy(isLoading = false, isButtonEnable = true)
                }
            }
        }
    }

    override fun descriptionChange(text: String) {
        updateState { copy(description = text) }
    }

    private fun handleUnknownError() {
        offerEvent(TransactionEvents.ShowToast(R.string.strange_error))
    }

    private companion object {
        const val TAG = "TransactionViewModel"
    }
}