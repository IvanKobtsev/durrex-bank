package nekit.corporation.history_impl.navigation

import dev.zacsweers.metro.Inject
import nekit.corporation.loan_details_api.LoanDetailsApi
import nekit.corporation.shell_main_api.MainShellApi

@Inject
class AllLoansNavigator(
    private val mainShellApi: MainShellApi,
    private val loanDetailsApi: LoanDetailsApi
) {

    fun onClose() = mainShellApi.back()

    fun onOpenDetails(id: Int) = mainShellApi.runScreen {
        loanDetailsApi.loanDetails(id)
    }
}