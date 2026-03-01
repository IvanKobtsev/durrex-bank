package nekit.corporation.presentation.processing

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.domain.Validator
import nekit.corporation.loan_processing.R
import nekit.corporation.navigation.LoanProcessingNavigation
import nekit.corporation.presentation.model.Field
import nekit.corporation.presentation.model.LoanProcessingEvent
import nekit.corporation.presentation.model.LoanProcessingState
import nekit.corporation.util.domain.common.BadRequestFailure
import nekit.corporation.util.domain.common.CommonBackendFailure
import nekit.corporation.util.domain.common.NoConnectionFailure
import nekit.corporation.util.domain.common.NotFoundFailure
import nekit.corporation.util.domain.common.ServerFailure
import nekit.corporation.util.domain.common.UnknownFailure
import nekit.corporation.utils.PhoneNumberUtils
import javax.inject.Inject

class LoanProcessingViewModel @Inject constructor(
    private val navigation: LoanProcessingNavigation,
    private val createLoanUseCase: CreateLoanUseCase,
) : StatefulViewModel<LoanProcessingState>() {

    init {
        observeButton()
    }

    override fun createInitialState(): LoanProcessingState {
        return LoanProcessingState(
            firstName = Field(
                text = "",
                error = null,
                isObserved = false
            ), Field(
                text = "",
                error = null,
                isObserved = false
            ), Field(
                text = "",
                error = null,
                isObserved = false
            ),
            isLoading = false,
            isButtonEnable = false,
            period = 0,
            amount = 0,
            percent = 0.0,
            isButtonObserved = false
        )
    }

    fun init(period: Int, amount: Int, percent: Double) {
        updateState {
            copy(
                period = period,
                amount = amount,
                percent = percent
            )
        }
    }

    @OptIn(FlowPreview::class)
    fun onFirstNameChange(firstName: String) {
        if (!currentScreenState.firstName.isObserved) {
            observeFirstName()
            updateState {
                copy(firstName = this.firstName.copy(isObserved = true))
            }
        }
        updateState {
            copy(firstName = this.firstName.copy(text = firstName))
        }
    }

    @OptIn(FlowPreview::class)
    fun onLastNameChange(lastName: String) {
        if (!currentScreenState.lastName.isObserved) {
            observesSecondName()
            updateState {
                copy(lastName = this.lastName.copy(isObserved = true))
            }
        }
        updateState {
            copy(lastName = this.lastName.copy(text = lastName))
        }
    }

    @OptIn(FlowPreview::class)
    fun onPhoneChange(phone: String) {
        if (!currentScreenState.phone.isObserved) {
            observePhone()
            updateState {
                copy(phone = this.phone.copy(isObserved = true))
            }
        }
        updateState {
            copy(
                phone = this.phone.copy(
                    text = PhoneNumberUtils.getFilteredPhone(phone)
                )
            )
        }
    }

    fun onCreateClick() {
        viewModelScope.launch {
            reduceError {
                val state = currentScreenState
                val result = createLoanUseCase.execute(
                    laon = CreateLoan(
                        amount = state.amount,
                        firstName = state.firstName.text,
                        lastName = state.lastName.text,
                        percent = state.percent,
                        period = state.period,
                        phoneNumber = state.phone.text
                    )
                )
                navigation.onLoanProcessingStateOpen(
                    isApproved = result.state == LoanState.APPROVED,
                    period = result.period,
                    amount = result.amount
                )
            }
        }
    }

    fun onBackClick() {
        navigation.onBack()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @FlowPreview
    private fun observeFirstName() = with(viewModelScope) {
        launch(Dispatchers.Default) {
            screenState.map { it.currentState }
                .distinctUntilChanged { lastValue, newValue ->
                    lastValue.firstName.text == newValue.firstName.text
                }
                .debounce(DELAY_BETWEEN_CHECKING)
                .mapLatest {
                    Validator.validateEmptyField(it.firstName.text)
                        ?: Validator.validateName(
                            it.firstName.text
                        )
                }
                .collect {
                    updateState {
                        copy(
                            firstName = firstName.copy(error = reduceValidationError(it))
                        )
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @FlowPreview
    private fun observesSecondName() = with(viewModelScope) {
        launch(Dispatchers.Default) {
            screenState.map { it.currentState }
                .distinctUntilChanged { lastValue, newValue ->
                    lastValue.lastName.text == newValue.lastName.text
                }
                .debounce(DELAY_BETWEEN_CHECKING)
                .mapLatest {
                    Validator.validateEmptyField(it.lastName.text)
                        ?: Validator.validateName(
                            it.lastName.text
                        )
                }
                .collect {
                    updateState {
                        copy(
                            lastName = lastName.copy(error = reduceValidationError(it))
                        )
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @FlowPreview
    private fun observePhone() = with(viewModelScope) {
        launch(Dispatchers.Default) {
            screenState.map { it.currentState }
                .distinctUntilChanged { lastValue, newValue ->
                    lastValue.phone.text == newValue.phone.text
                }
                .debounce(DELAY_BETWEEN_CHECKING)
                .mapLatest {
                    Validator.validateEmptyField(it.phone.text)
                        ?: Validator.validatePhone(
                            it.phone.text
                        )
                }
                .collect {
                    updateState {
                        copy(
                            phone = phone.copy(error = reduceValidationError(it))
                        )
                    }
                }
        }
    }

    private fun observeButton() {
        viewModelScope.launch {
            screenState.collect {
                updateState {
                    copy(
                        isButtonEnable =
                            firstName.error != null &&
                                    lastName.error != null &&
                                    phone.error != null &&
                                    firstName.text.isNotEmpty() &&
                                    lastName.text.isNotEmpty() &&
                                    phone.text.isNotEmpty()
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
                is BadRequestFailure, is NotFoundFailure -> {
                    offerEvent(LoanProcessingEvent.ShowToast(R.string.loan_bad_request))
                }

                is NoConnectionFailure -> {
                    offerEvent(LoanProcessingEvent.ShowToast(R.string.loan_strange_error))
                    navigation.onBack()
                }

                is ServerFailure, is UnknownFailure -> {
                    offerEvent(LoanProcessingEvent.ShowToast(R.string.loan_strange_error))
                }

            }
        } catch (e: Throwable) {
            offerEvent(LoanProcessingEvent.ShowToast(R.string.loan_strange_error))
            Log.d(TAG, e.message.toString())
        }
    }

    private companion object {
        const val DELAY_BETWEEN_CHECKING = 300L
        const val TAG = "LoanProcessingViewModel"
    }
}
