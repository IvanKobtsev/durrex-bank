package nekit.corporation.account_details_impl.navigation

import com.github.terrakok.cicerone.Router
import dev.zacsweers.metro.Inject
import nekit.corporation.auth_api.AuthApi
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.shell_main_api.MainShellApi
import nekit.corporation.shell_main_api.model.Tab
import nekit.corporation.transaction_details_api.TransactionDetailsApi

@Inject
class AccountDetailsNavigator(
    private val mainShellApi: MainShellApi,
    private val transactionDetailsApi: TransactionDetailsApi,
    private val router: Router,
    private val authApi: AuthApi
) {

    fun back() {
        mainShellApi.back()
    }

    fun toTransaction(accountId: Int, transactionId: Long) {
        mainShellApi.runScreen {
            transactionDetailsApi.transactionDetails(accountId, transactionId)
        }
    }

    fun toAuth() = router.newRootScreen(authApi.auth())
}

