package nekit.corporation.history_impl.presentation.all.loans

import androidx.lifecycle.ViewModel
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
import nekit.corporation.loan_shared.domain.usecase.GetCreditsUseCase
import nekit.corporation.history_impl.navigation.AllLoansNavigator
import nekit.corporation.history_impl.presentation.models.AllLoansState

@Inject
@ViewModelKey(AllLoansViewModel::class)
@ContributesIntoMap(
    AppScope::class, binding = binding<ViewModel>()
)
class AllLoansViewModel(
    private val allLoansNavigation: AllLoansNavigator,
    private val getLoansUseCase: GetCreditsUseCase
) : StatefulViewModel<AllLoansState>() {

    override fun createInitialState(): AllLoansState {
        return AllLoansState.Loading
    }

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            updateState {
                AllLoansState.Loading
            }
            val loans = getLoansUseCase()
            updateState {
                AllLoansState.Component(
                    loans = loans.toImmutableList()
                )
            }

        }
    }

    fun onClose() = allLoansNavigation.onClose()

    fun onOpenLoan(id: Int) = allLoansNavigation.onOpenDetails(id)
}