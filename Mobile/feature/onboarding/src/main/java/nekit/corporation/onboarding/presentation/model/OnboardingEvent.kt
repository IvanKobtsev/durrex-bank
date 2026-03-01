package nekit.corporation.onboarding.presentation.model

import nekit.corporation.architecture.presentation.Event

sealed interface OnboardingEvent : Event {

    data class ChangePage(val page: Int) : OnboardingEvent
}
