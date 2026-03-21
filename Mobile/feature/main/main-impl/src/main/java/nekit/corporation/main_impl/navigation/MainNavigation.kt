package nekit.corporation.main_impl.navigation

import com.github.terrakok.cicerone.Router
import dev.zacsweers.metro.Inject
import nekit.corporation.account_details_api.AccountDetailsApi
import nekit.corporation.create_loan_api.CreateLoanApi
import nekit.corporation.history_api.HistoryApi
import nekit.corporation.loan_details_api.LoanDetailsApi
import nekit.corporation.onboarding.OnboardingApi
import nekit.corporation.shell_main_api.MainShellApi

@Inject
internal class MainNavigation(
    private val router: Router,
    private val mainShellApi: MainShellApi,
    private val historyApi: HistoryApi,
    private val onboardingApi: OnboardingApi,
    private val accountDetailsApi: AccountDetailsApi,
    private val loanDetailsApi: LoanDetailsApi,
    private val createLoanApi: CreateLoanApi
) {

    fun openAllLoans() = mainShellApi.runScreen {
        historyApi.allLoans()
    }

    fun openAllAccounts() = mainShellApi.runScreen {
        historyApi.allAccounts()
    }


    fun openLoanById(id: Int) = mainShellApi.runScreen {
        loanDetailsApi.loanDetails(id)
    }


    fun openAccountById(id: Int) = mainShellApi.runScreen {
        accountDetailsApi.accountDetails(id)
    }

    fun openOnboarding() = router.navigateTo(onboardingApi.onboarding())

    fun openCreateCredit() = router.navigateTo(createLoanApi.createLoan())
}
