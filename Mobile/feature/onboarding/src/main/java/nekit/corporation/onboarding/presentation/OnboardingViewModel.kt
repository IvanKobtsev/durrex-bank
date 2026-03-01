package nekit.corporation.onboarding.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.onboarding.R
import nekit.corporation.onboarding.presentation.model.OnboardingEvent
import nekit.corporation.onboarding.presentation.model.OnboardingState
import nekit.corporation.onboarding.presentation.model.Page
import nekit.corporation.onboarding.navigation.OnboardingNavigation
import nekit.corporation.onboarding_shared.domain.usecase.UpdateSettingUseCase
import javax.inject.Inject

class OnboardingViewModel @Inject constructor(
    private val onboardingNavigation: OnboardingNavigation,
    private val updateSettingUseCase: UpdateSettingUseCase
) :
    StatefulViewModel<OnboardingState>() {
    override fun createInitialState(): OnboardingState {
        return OnboardingState(
            persistentListOf(
                Page(
                    image = R.drawable.create_loan_img,
                    label = R.string.create_loan,
                    description = R.string.create_loan_description
                ),
                Page(
                    image = R.drawable.get_loan_img,
                    label = R.string.get_loan,
                    description = R.string.get_loan_description
                ),
                Page(
                    image = R.drawable.created_loans_img,
                    label = R.string.created_loans,
                    description = R.string.created_loans_description
                ),
            ),
            0
        )
    }

    fun onContinueClick() {
        if (screenState.value.currentState.currentPage == screenState.value.currentState.pages.size - 1) {
            onOnboardingFinish()
            onboardingNavigation.openMain()
        } else {
            val newPage = screenState.value.currentState.currentPage + 1
            updateState {
                copy(currentPage = newPage)
            }
            offerEvent(OnboardingEvent.ChangePage(newPage))
        }
    }

    fun onBackClick() {
        val newPage = screenState.value.currentState.currentPage - 1
        updateState {
            copy(currentPage = newPage)
        }
        offerEvent(OnboardingEvent.ChangePage(newPage))
    }

    fun onSkipClick() {
        onOnboardingFinish()
        onboardingNavigation.openMain()
    }

    fun onSwipe(currentPage: Int) {
        updateState {
            copy(
                currentPage = currentPage
            )
        }
    }

    private fun onOnboardingFinish() {
        viewModelScope.launch(Dispatchers.IO) {
            updateSettingUseCase.execute { settingsModel ->
                settingsModel.copy(isShowedOnboarding = true)
            }
        }
    }
}