package com.example.shift_project.presentation.navigation

import com.github.terrakok.cicerone.Router
import nekit.corporation.create_loan.navigation.CreateCreditNavigation
import javax.inject.Inject

class CreateCreditNavigator @Inject constructor(
    private val router: Router
) : CreateCreditNavigation {

    override fun onBack() {
        router.exit()
    }
}