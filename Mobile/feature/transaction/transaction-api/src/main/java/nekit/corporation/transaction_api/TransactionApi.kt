package nekit.corporation.transaction_api

import com.github.terrakok.cicerone.Screen

interface TransactionApi {

    fun transaction(): Screen
}