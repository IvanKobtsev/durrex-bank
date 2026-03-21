package nekit.corporation.transaction_details_impl.navigation

import com.github.terrakok.cicerone.Router
import dev.zacsweers.metro.Inject
import nekit.corporation.auth_api.AuthApi

@Inject
class TransactionDetailsNavigator(
    private val router: Router,
    private val authApi: AuthApi
) {

    fun onBack() = router.exit()

    fun toAuth() = router.newRootScreen(authApi.auth())
}