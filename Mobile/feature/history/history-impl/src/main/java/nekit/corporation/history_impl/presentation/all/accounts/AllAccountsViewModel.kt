package nekit.corporation.history_impl.presentation.all.accounts

import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.history_impl.navigation.AllAccountsNavigator
import nekit.corporation.history_impl.presentation.models.AllAccountsState

@Inject
@ViewModelKey(AllAccountsViewModel::class)
@ContributesIntoMap(
    AppScope::class,
    binding<@ClassKey(AllAccountsViewModel::class) StatefulViewModel<AllAccountsState>>()
)
internal class AllAccountsViewModel(
    private val allActionsNavigation: AllAccountsNavigator,
    private val accountRepository: AccountRepository,
) : StatefulViewModel<AllAccountsState>() {

    override fun createInitialState(): AllAccountsState {
        return AllAccountsState.Loading
    }

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {

            val accounts = accountRepository.getAllAccounts()
            updateState {
                AllAccountsState.Component(
                    accounts = accounts.toImmutableList()
                )
            }
        }
    }


    fun onClose() = allActionsNavigation.onClose()

    fun onOpenAccount(id: Int) = allActionsNavigation.onOpenDetails(id)
}