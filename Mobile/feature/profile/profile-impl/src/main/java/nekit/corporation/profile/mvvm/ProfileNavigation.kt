package nekit.corporation.profile.mvvm

import dev.zacsweers.metro.Inject
import nekit.corporation.shell_main_api.MainShellApi
import nekit.corporation.shell_main_api.model.Tab

@Inject
internal class ProfileNavigation(
    private val mainShellApi: MainShellApi
) {

    fun toMain() = mainShellApi.onTab(Tab.Main)
}