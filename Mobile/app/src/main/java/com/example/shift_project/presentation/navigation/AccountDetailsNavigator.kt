package com.example.shift_project.presentation.navigation

import com.example.shift_project.presentation.navigation.Screens.transactionDetails
import nekit.corporation.navigation.AccountDetailsNavigation
import nekit.corporation.navigation.MainBottomBarRouter
import javax.inject.Inject

class AccountDetailsNavigator @Inject constructor(
    private val router: MainBottomBarRouter
) : AccountDetailsNavigation {

    override fun onBack() {
        router.exit()
    }

    override fun toTransaction(accountId: Int, transactionId: Long) {
        router.navigateTo(transactionDetails(accountId, transactionId))
    }
}