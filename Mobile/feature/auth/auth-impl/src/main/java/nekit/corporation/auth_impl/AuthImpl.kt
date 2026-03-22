package nekit.corporation.auth_impl

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
    val viewModelFactory: ViewModelProvider.Factory,
    private var authManager: AuthManager
) : AuthApi {

    override fun auth() = FragmentScreen {
        AuthFragment(viewModelFactory,authManager)
    }
}