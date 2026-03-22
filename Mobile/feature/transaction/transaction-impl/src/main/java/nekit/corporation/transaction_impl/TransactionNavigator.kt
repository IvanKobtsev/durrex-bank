package nekit.corporation.transaction_impl

import dev.zacsweers.metro.Inject
import nekit.corporation.shell_main_api.MainShellApi
import nekit.corporation.shell_main_api.model.Tab

@Inject
class TransactionNavigator(
    private val shellMainApi: MainShellApi,
) {

    fun toMain() = shellMainApi.onTab(Tab.Main)
}