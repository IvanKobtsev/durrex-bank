package com.example.shift_project.presentation.navigation

import com.example.shift_project.presentation.navigation.Screens.accountDetails
import jakarta.inject.Inject
import nekit.corporation.navigation.AllAccountsNavigation
import nekit.corporation.navigation.MainBottomBarRouter

class AllAccountsNavigator @Inject constructor(
    private val mainBottomBarRouter: MainBottomBarRouter,
) : AllAccountsNavigation {

    override fun onClose() {
        mainBottomBarRouter.exit()
    }

    override fun onOpenDetails(id: Int) {
        mainBottomBarRouter.navigateTo(accountDetails(id = id))
    }
}