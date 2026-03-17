package com.example.shift_project.presentation.navigation

import nekit.corporation.navigation.LoanDetailsNavigation
import nekit.corporation.navigation.MainBottomBarRouter
import javax.inject.Inject

class LoanDetailsNavigator @Inject constructor(
    private val router: MainBottomBarRouter
) : LoanDetailsNavigation {

    override fun onBack() {
        router.exit()
    }
}