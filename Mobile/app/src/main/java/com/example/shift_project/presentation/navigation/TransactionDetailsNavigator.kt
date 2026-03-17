package com.example.shift_project.presentation.navigation

import com.github.terrakok.cicerone.Router
import nekit.corporation.transaction_details.navigation.TransactionDetailsNavigation
import javax.inject.Inject

class TransactionDetailsNavigator @Inject constructor(
    private val router: Router
) : TransactionDetailsNavigation {

    override fun onBack() {
        router.exit()
    }
}