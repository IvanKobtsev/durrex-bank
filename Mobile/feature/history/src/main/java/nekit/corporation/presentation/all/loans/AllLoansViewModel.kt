package nekit.corporation.presentation.all.loans

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.loan_shared.domain.usecase.GetCreditsUseCase
import nekit.corporation.navigation.AllLoansNavigation
import nekit.corporation.presentation.models.AllLoansState
import javax.inject.Inject

class AllLoansViewModel @Inject constructor(
    private val allLoansNavigation: AllLoansNavigation,
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

    private companion object {
        const val BASE_LOCALE = "ru"
    }
}