package nekit.corporation.onboarding_impl.navigation

import dev.zacsweers.metro.Inject
import nekit.corporation.shell_main_api.MainShellApi
import nekit.corporation.shell_main_api.model.Tab

@Inject
class OnboardingNavigator(
    private val mainShellApi: MainShellApi
) {

    fun openMain() = mainShellApi.onTab(Tab.Main)
}