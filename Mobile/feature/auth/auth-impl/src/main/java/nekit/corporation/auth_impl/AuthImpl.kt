package nekit.corporation.auth_impl

import androidx.lifecycle.ViewModelProvider
import com.github.terrakok.cicerone.androidx.FragmentScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import nekit.corporation.auth_api.AuthApi
import nekit.corporation.auth_impl.presentation.auth.AuthFragment


@ContributesBinding(AppScope::class)
class AuthImpl(val viewModelFactory: ViewModelProvider.Factory) : AuthApi {

    override fun auth() = FragmentScreen {
        AuthFragment(viewModelFactory)
    }
}