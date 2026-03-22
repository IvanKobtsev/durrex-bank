package nekit.corporation.history_impl.navigation

import dev.zacsweers.metro.Inject
import nekit.corporation.account_details_api.AccountDetailsApi
import nekit.corporation.shell_main_api.MainShellApi

@Inject
class AllAccountsNavigator(
    private val mainShellApi: MainShellApi,
    private val accountDetailsApi: AccountDetailsApi
) {

    fun onClose() = mainShellApi.back()

    fun onOpenDetails(id: Int) = mainShellApi.runScreen {
        accountDetailsApi.accountDetails(id)
    }
}