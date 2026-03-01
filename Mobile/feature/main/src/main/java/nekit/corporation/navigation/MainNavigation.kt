package nekit.corporation.navigation

interface MainNavigation {

    fun openLoanCreating(amount: Int, percent: Double, period: Int)

    fun openAllLoans()

    fun openLoanById(id: Int)

    fun openOnboarding()

    fun openCreateCredit()
}
