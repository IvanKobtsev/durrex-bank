package nekit.corporation.`onboarding-impl`.presentation.model

import kotlinx.collections.immutable.ImmutableList
import nekit.corporation.architecture.presentation.ScreenState

internal data class OnboardingState(
    val pages: ImmutableList<Page>,
    val currentPage: Int
) : ScreenState
