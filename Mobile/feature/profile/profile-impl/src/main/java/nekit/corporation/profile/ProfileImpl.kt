package nekit.corporation.profile

import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import nekit.corporation.ProfileApi
import nekit.corporation.common.AppScope

@ContributesBinding(AppScope::class)
class ProfileImpl : ProfileApi {

    override fun profile(): Screen {
        return FragmentScreen { ProfileFragment() }
    }
}