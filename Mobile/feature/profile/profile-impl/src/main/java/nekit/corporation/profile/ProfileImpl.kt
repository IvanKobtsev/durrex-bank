package nekit.corporation.profile

import androidx.lifecycle.ViewModelProvider
import com.github.terrakok.cicerone.androidx.FragmentScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import nekit.corporation.ProfileApi

@Inject
@ContributesBinding(AppScope::class)
class ProfileImpl(
    private val viewModelFactory: ViewModelProvider.Factory
) : ProfileApi {

    override fun profile() = FragmentScreen { ProfileFragment(viewModelFactory) }
}
