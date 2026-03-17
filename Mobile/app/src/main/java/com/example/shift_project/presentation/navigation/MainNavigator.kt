package com.example.shift_project.presentation.navigation

import com.example.shift_project.presentation.navigation.Screens.accountDetails
import com.example.shift_project.presentation.navigation.Screens.allAccounts
import com.example.shift_project.presentation.navigation.Screens.allLoans
import com.example.shift_project.presentation.navigation.Screens.createCredit
import com.example.shift_project.presentation.navigation.Screens.creditDetails
import com.example.shift_project.presentation.navigation.Screens.onboarding
import com.github.terrakok.cicerone.Router
import nekit.corporation.navigation.MainBottomBarRouter
import nekit.corporation.navigation.MainNavigation
import javax.inject.Inject

class MainNavigator @Inject constructor(
    private val router: Router,
    private val bottomBarRouter: MainBottomBarRouter,
) : MainNavigation {


    override fun openAllLoans() {
        bottomBarRouter.navigateTo(allLoans())
    }

    override fun openAllAccounts() {
        bottomBarRouter.navigateTo(allAccounts())
    }


    override fun openLoanById(id: Int) {
        bottomBarRouter.navigateTo(screen = creditDetails(id))
    }

    override fun openAccountById(id: Int) {
        bottomBarRouter.navigateTo(screen = accountDetails(id))
    }

    override fun openOnboarding() {
        router.replaceScreen(screen = onboarding())
    }

    override fun openCreateCredit() {
        router.navigateTo(screen = createCredit())
    }
}
