package nekit.corporation.presentation.menu

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.navigation.MenuNavigation
import nekit.corporation.presentation.models.MenuState
import nekit.corporation.user.domain.UserRepository
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
    private val navigation: MenuNavigation,
    private val userRepository: UserRepository
) : StatefulViewModel<MenuState>() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val transactions = userRepository.getTransactions(
                userRepository.getUser().id
            )
            updateState {
                copy(transactions = transactions.items.toImmutableList())
            }

        }
    }

    override fun createInitialState(): MenuState {
        return MenuState(
            transactions = persistentListOf(),
        )
    }

    fun openOnboarding() {
        navigation.openOnboarding()
    }
}