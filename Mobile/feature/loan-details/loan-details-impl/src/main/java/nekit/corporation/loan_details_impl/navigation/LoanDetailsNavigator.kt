package nekit.corporation.loan_details_impl.navigation

import com.github.terrakok.cicerone.Router
import dev.zacsweers.metro.Inject
import nekit.corporation.auth_api.AuthApi
import nekit.corporation.shell_main_api.MainShellApi

@Inject
class LoanDetailsNavigator(
    private val mainShellApi: MainShellApi,
    private val router: Router,
    private val authApi: AuthApi
) {

    fun onBack() = mainShellApi.back()

    fun onAuth() = router.newRootScreen(authApi.auth())
}