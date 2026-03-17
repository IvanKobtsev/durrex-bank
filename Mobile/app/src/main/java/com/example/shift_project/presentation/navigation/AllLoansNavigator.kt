package com.example.shift_project.presentation.navigation

import com.example.shift_project.presentation.navigation.Screens.creditDetails
import nekit.corporation.navigation.AllLoansNavigation
import nekit.corporation.navigation.MainBottomBarRouter
import javax.inject.Inject

class AllLoansNavigator @Inject constructor(
    private val mainBottomBarRouter: MainBottomBarRouter,
) : AllLoansNavigation {

    override fun onClose() {
        mainBottomBarRouter.exit()
    }

    override fun onOpenDetails(id: Int) {
        mainBottomBarRouter.navigateTo(creditDetails(id = id))
    }
}