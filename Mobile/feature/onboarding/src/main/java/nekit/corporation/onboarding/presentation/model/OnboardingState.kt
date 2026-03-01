package nekit.corporation.onboarding.presentation.model

import kotlinx.collections.immutable.ImmutableList
import nekit.corporation.architecture.presentation.ScreenState

data class OnboardingState(
    val pages: ImmutableList<Page>,
    val currentPage: Int
) : ScreenState
