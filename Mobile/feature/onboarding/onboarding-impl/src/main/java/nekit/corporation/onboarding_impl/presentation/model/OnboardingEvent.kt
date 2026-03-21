package nekit.corporation.onboarding_impl.presentation.model

import nekit.corporation.architecture.presentation.Event

internal sealed interface OnboardingEvent : Event {

    data class ChangePage(val page: Int) : OnboardingEvent
}
