package nekit.corporation.navigation

interface LoanProcessingNavigation {

    fun onBack()

    fun onLoanProcessingStateOpen(isApproved: Boolean, period: Int, amount: Int)
}