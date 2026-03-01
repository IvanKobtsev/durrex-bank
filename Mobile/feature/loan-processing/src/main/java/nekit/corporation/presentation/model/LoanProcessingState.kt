package nekit.corporation.presentation.model

import nekit.corporation.architecture.presentation.ScreenState

data class LoanProcessingState(
    val firstName: Field,
    val lastName: Field,
    val phone: Field,
    val isLoading: Boolean,
    val isButtonEnable: Boolean,
    val period: Int,
    val amount: Int,
    val percent: Double,
    val isButtonObserved: Boolean
) : ScreenState
