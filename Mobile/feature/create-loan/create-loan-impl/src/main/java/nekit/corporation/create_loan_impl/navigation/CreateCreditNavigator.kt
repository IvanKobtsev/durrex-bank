package nekit.corporation.create_loan_impl.navigation

import com.github.terrakok.cicerone.Router
import dev.zacsweers.metro.Inject
import nekit.corporation.shell_main_api.MainShellApi
import nekit.corporation.shell_main_api.model.Tab

@Inject
class CreateCreditNavigator(
    private val router: Router,
    private val mainShellApi: MainShellApi
) {

    fun onBack() = router.newRootScreen(mainShellApi.onTab(Tab.Main))
}