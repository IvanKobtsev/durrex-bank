package nekit.corporation.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.loan_details.R
import nekit.corporation.loan_shared.domain.usecase.GetCreditDetailUseCase
import nekit.corporation.navigation.LoanDetailsNavigation
import nekit.corporation.presentation.model.LoanDetailsEvent
import nekit.corporation.presentation.model.LoanDetailsState
import nekit.corporation.user.domain.GetUserUseCase
import nekit.corporation.util.domain.common.BadRequestFailure
import nekit.corporation.util.domain.common.CommonBackendFailure
import nekit.corporation.util.domain.common.NoConnectionFailure
import nekit.corporation.util.domain.common.NotFoundFailure
import nekit.corporation.util.domain.common.ServerFailure
import nekit.corporation.util.domain.common.UnknownFailure
import javax.inject.Inject

class LoanDetailsViewModel @Inject constructor(
    private val getCreditByIdUseCase: GetCreditDetailUseCase,
    private val userUserUseCase: GetUserUseCase,
    private val loanDetailsNavigation: LoanDetailsNavigation
) : StatefulViewModel<LoanDetailsState>() {

    override fun createInitialState(): LoanDetailsState {
        return LoanDetailsState.Loading
    }

    fun onBack() = loanDetailsNavigation.onBack()

    fun init(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            reduceError {
                val user = userUserUseCase()
                val credit = getCreditByIdUseCase(
                    user.id,
                    userRole = user.role.name,
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
                    offerEvent(LoanDetailsEvent.ShowToast(R.string.no_connections_error))
                }

                is NotFoundFailure -> {
                    offerEvent(LoanDetailsEvent.ShowToast(R.string.not_found_error))
                }

                is ServerFailure, is UnknownFailure, is BadRequestFailure ->
                    offerEvent(LoanDetailsEvent.ShowToast(R.string.strange_error))

            }
        } catch (e: Throwable) {
            offerEvent(LoanDetailsEvent.ShowToast(R.string.strange_error))
            Log.d(TAG, e.message.toString())
        }
    }

    private companion object {
        const val TAG = "LoanDetailsViewModel"
    }
}