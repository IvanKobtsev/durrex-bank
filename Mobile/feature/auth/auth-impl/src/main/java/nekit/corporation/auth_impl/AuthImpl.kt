package nekit.corporation.auth_impl

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.github.terrakok.cicerone.androidx.FragmentScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import nekit.corporation.auth_api.AuthApi
import nekit.corporation.auth_impl.presentation.auth.AuthFragment


@Inject
@ContributesBinding(AppScope::class)
class AuthImpl(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val authManager: AuthManager
) : AuthApi {

    override fun auth() = FragmentScreen {
        AuthFragment(viewModelFactory, authManager)
    }

    override fun getLogoutIntent(): Intent? {
        return authManager.logoutIntent()
    }
}