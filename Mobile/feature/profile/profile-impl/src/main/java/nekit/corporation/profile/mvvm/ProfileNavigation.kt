package nekit.corporation.profile.mvvm

import com.github.terrakok.cicerone.Router
import dev.zacsweers.metro.Inject
import nekit.corporation.auth_api.AuthApi

@Inject
class ProfileNavigation(
    private val authApi: AuthApi,
    private val router: Router
) {

    fun toAuth() = router.newRootScreen(authApi.auth())
}