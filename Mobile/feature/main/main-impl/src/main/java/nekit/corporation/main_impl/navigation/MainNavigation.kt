package nekit.corporation.navigation

interface MainNavigation {

    fun openAllLoans()

    fun openAllAccounts()

    fun openLoanById(id: Int)

    fun openAccountById(id: Int)

    fun openOnboarding()

    fun openCreateCredit()
}
