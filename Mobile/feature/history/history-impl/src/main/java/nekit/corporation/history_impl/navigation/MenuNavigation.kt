package nekit.corporation.navigation

interface MenuNavigation {

    fun openOnboarding()

    fun openLoans()

    fun openDetails(accountId:Int,transactionId:Long)
}