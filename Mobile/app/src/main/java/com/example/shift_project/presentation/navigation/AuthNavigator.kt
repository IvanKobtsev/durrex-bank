package com.example.shift_project.presentation.navigation

import com.example.shift_project.presentation.navigation.Screens.mainShell
import com.example.shift_project.presentation.navigation.Screens.onboarding
import com.github.terrakok.cicerone.Router
import nekit.corporation.auth.navigation.AuthNavigation
import javax.inject.Inject

class AuthNavigator @Inject constructor(private val router: Router) : AuthNavigation {

    override fun openOnboarding() {
        router.newRootScreen(onboarding())
    }

    override fun openMain() {
        router.newRootScreen(mainShell())
    }
}