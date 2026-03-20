package nekit.corporation.navigation

interface AccountDetailsNavigation {

    fun onBack()

   fun  toTransaction(accountId: Int, transactionId: Long)
}