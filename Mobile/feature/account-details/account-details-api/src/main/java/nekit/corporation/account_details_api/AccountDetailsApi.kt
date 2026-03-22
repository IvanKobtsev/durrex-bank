package nekit.corporation.account_details_api

import com.github.terrakok.cicerone.Screen

interface AccountDetailsApi {

    fun accountDetails(id: Int): Screen
}