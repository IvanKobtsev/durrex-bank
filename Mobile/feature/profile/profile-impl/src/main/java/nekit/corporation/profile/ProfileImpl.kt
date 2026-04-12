package nekit.corporation.profile

import com.github.terrakok.cicerone.androidx.FragmentScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import nekit.corporation.ProfileApi

@Inject
@ContributesBinding(AppScope::class)
class ProfileImpl(
    private val profileFragment: ProfileFragment
) : ProfileApi {

    override fun profile() = FragmentScreen { profileFragment }
}
