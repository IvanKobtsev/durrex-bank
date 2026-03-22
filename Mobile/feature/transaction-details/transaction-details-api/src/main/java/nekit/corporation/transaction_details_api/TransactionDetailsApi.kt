package nekit.corporation.transaction_details_api

import com.github.terrakok.cicerone.Screen

interface TransactionDetailsApi {

    fun transactionDetails(accountId: Int, transactionId: Long): Screen
}