package com.example.shift_project.presentation.navigation

import com.example.shift_project.presentation.navigation.Screens.allLoans
import com.example.shift_project.presentation.navigation.Screens.onboarding
import com.example.shift_project.presentation.navigation.Screens.transactionDetails
import com.github.terrakok.cicerone.Router
import nekit.corporation.navigation.MainBottomBarRouter
import nekit.corporation.navigation.MenuNavigation
import javax.inject.Inject

class MenuNavigator @Inject constructor(
    private val router: Router,
    private val mainBottomBarRouter: MainBottomBarRouter,
) : MenuNavigation {

    override fun openOnboarding() {
        router.replaceScreen(onboarding())
    }

    override fun openLoans() {
        mainBottomBarRouter.navigateTo(allLoans())
    }

    override fun openDetails(accountId: Int, transactionId: Long) {
        router.navigateTo(transactionDetails(accountId, transactionId))
    }
}
