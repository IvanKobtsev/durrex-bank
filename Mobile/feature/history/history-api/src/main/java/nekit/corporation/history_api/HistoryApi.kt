package nekit.corporation.history_api

import com.github.terrakok.cicerone.Screen

interface HistoryApi {

    fun history(): Screen

    fun allAccounts(): Screen

    fun allLoans(): Screen
}