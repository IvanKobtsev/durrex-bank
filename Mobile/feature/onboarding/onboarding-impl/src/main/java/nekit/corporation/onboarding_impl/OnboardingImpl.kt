package nekit.corporation.onboarding_impl

import androidx.lifecycle.ViewModelProvider
import com.github.terrakok.cicerone.androidx.FragmentScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import nekit.corporation.onboarding.OnboardingApi
import nekit.corporation.onboarding_impl.presentation.OnboardingFragment

@Inject
@ContributesBinding(AppScope::class)
class OnboardingImpl(
    private val viewModelFactory: ViewModelProvider.Factory
) : OnboardingApi {

    override fun onboarding() = FragmentScreen {
        OnboardingFragment(viewModelFactory)
    }
}