package nekit.corporation.loan_details_api

import com.github.terrakok.cicerone.Screen

interface LoanDetailsApi {

    fun loanDetails(id: Int): Screen
}