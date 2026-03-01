package nekit.corporation.presentation.all.accounts

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.navigation.AllAccountsNavigation
import nekit.corporation.presentation.models.AllAccountsState
import nekit.corporation.user.domain.UserRepository
import javax.inject.Inject

class AllAccountsViewModel @Inject constructor(
    private val allActionsNavigation: AllAccountsNavigation,
    private val userRepository: UserRepository,
    private val accountRepository: AccountRepository,
) : StatefulViewModel<AllAccountsState>() {

    override fun createInitialState(): AllAccountsState {
        return currentScreenState
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            updateState {
                AllAccountsState.Loading
            }
            val user = userRepository.getUser()
            val accounts = accountRepository.getAllAccounts(user.id)
            updateState {
                AllAccountsState.Component(
                    accounts = accounts.toImmutableList()
                )
            }
        }
    }


    fun onClose() = allActionsNavigation.onClose()

    fun onOpenAccount(id: Int) = allActionsNavigation.onOpenDetails(id)

    private companion object {
        const val BASE_LOCALE = "ru"
    }
}