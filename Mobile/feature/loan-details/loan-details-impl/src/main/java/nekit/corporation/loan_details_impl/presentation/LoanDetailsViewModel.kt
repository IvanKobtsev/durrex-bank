package nekit.corporation.loan_details_impl.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.loan_details_impl.R
import nekit.corporation.loan_shared.domain.usecase.GetCreditDetailUseCase
import nekit.corporation.loan_details_impl.navigation.LoanDetailsNavigator
import nekit.corporation.loan_details_impl.presentation.model.LoanDetailsEvent.*
import nekit.corporation.loan_details_impl.presentation.model.LoanDetailsState
import nekit.corporation.loan_details_impl.presentation.model.LoanInteractions
import nekit.corporation.util.domain.common.BadRequestFailure
import nekit.corporation.util.domain.common.CommonBackendFailure
import nekit.corporation.util.domain.common.ForbiddenFailure
import nekit.corporation.util.domain.common.NoConnectionFailure
import nekit.corporation.util.domain.common.NotFoundFailure
import nekit.corporation.util.domain.common.ServerFailure
import nekit.corporation.util.domain.common.UnknownFailure

@Inject
@ViewModelKey(LoanDetailsViewModel::class)
@ContributesIntoMap(AppScope::class, binding<@ClassKey(LoanDetailsViewModel::class) LoanInteractions>())
internal class LoanDetailsViewModel(
    private val getCreditByIdUseCase: GetCreditDetailUseCase,
    private val navigator: LoanDetailsNavigator
) : StatefulViewModel<LoanDetailsState>(), LoanInteractions {

    override fun createInitialState(): LoanDetailsState {
        return LoanDetailsState.Loading
    }

    override fun onBack() = navigator.onBack()

    fun init(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            reduceError {
                val credit = getCreditByIdUseCase(
                    creditId = id
                )

                updateState {
                    LoanDetailsState.Content(
                        credit = credit
                    )
                }
            }
        }
    }

    private suspend fun reduceError(block: suspend () -> Unit) {
        try {
            block()
        } catch (e: CommonBackendFailure) {
            when (e) {
                is NoConnectionFailure -> {
                    offerEvent(ShowToast(R.string.no_connections_error))
                }

                is NotFoundFailure -> {
                    offerEvent(ShowToast(R.string.not_found_error))
                }

                is ServerFailure, is UnknownFailure, is BadRequestFailure ->
                    offerEvent(ShowToast(R.string.strange_error))

                is ForbiddenFailure -> navigator.onAuth()
            }
        } catch (e: Throwable) {
            offerEvent(ShowToast(R.string.strange_error))
            Log.d(TAG, e.message.toString())
        }
    }

    private companion object {
        const val TAG = "LoanDetailsViewModel"
    }
}