package nekit.corporation.auth_impl.navigation

import com.github.terrakok.cicerone.Router
import dev.zacsweers.metro.Inject
import nekit.corporation.onboarding.OnboardingApi
import nekit.corporation.shell_main_api.MainShellApi
import nekit.corporation.shell_main_api.model.Tab

@Inject
class AuthNavigator(
    private val router: Router,
    private val mainShellApi: MainShellApi,
    private val onboardingApi: OnboardingApi
) {

    fun onMainOpen() {
        router.newRootScreen(mainShellApi.onTab(Tab.Main))
    }

    fun onOnboardingOpen() {
        router.newRootScreen(onboardingApi.onboarding())
    }
}