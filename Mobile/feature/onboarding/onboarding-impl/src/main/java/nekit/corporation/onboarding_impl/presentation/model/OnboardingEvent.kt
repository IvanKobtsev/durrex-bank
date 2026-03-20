package nekit.corporation.onboarding.presentation.model

import nekit.corporation.architecture.presentation.Event

internal sealed interface OnboardingEvent : Event {

    data class ChangePage(val page: Int) : OnboardingEvent
}
