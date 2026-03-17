package com.example.shift_project.presentation.navigation

import com.example.shift_project.presentation.navigation.Screens.mainShell
import com.github.terrakok.cicerone.Router
import nekit.corporation.navigation.Screens.main
import nekit.corporation.onboarding.navigation.OnboardingNavigation
import javax.inject.Inject

class OnboardingNavigator @Inject constructor(
    private val router: Router
): OnboardingNavigation {

    override fun openMain() {
        router.replaceScreen(mainShell())
    }
}
