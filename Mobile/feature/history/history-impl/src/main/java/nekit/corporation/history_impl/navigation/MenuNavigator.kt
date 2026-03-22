package nekit.corporation.history_impl.navigation

import com.github.terrakok.cicerone.Router
import dev.zacsweers.metro.Inject
import nekit.corporation.auth_api.AuthApi
import nekit.corporation.history_api.HistoryApi
import nekit.corporation.onboarding.OnboardingApi
import nekit.corporation.shell_main_api.MainShellApi
import nekit.corporation.transaction_details_api.TransactionDetailsApi

@Inject
internal class MenuNavigator(
    private val router: Router,
    private val authApi: AuthApi,
    private val onboardingApi: OnboardingApi,
    private val transactionDetailsApi: TransactionDetailsApi,
    private val historyApi: HistoryApi,
    private val mainShellApi: MainShellApi
) {

    fun openOnboarding() {
        router.replaceScreen(onboardingApi.onboarding())
    }

    fun openLoans() {
        mainShellApi.runScreen {
            historyApi.allLoans()
        }
    }

    fun openAuth() {
        router.newRootScreen(authApi.auth())
    }

    fun openDetails(accountId: Int, transactionId: Long) {
        router.navigateTo(transactionDetailsApi.transactionDetails(accountId, transactionId))
    }
}