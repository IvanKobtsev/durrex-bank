package nekit.corporation.onboarding_impl.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import nekit.corporation.common.di.FragmentKey
import nekit.corporation.onboarding_impl.presentation.ui.OnboardingScreen
import nekit.corporation.ui.theme.DurexBankTheme

@ContributesIntoMap(AppScope::class)
@FragmentKey(OnboardingFragment::class)
@Inject
internal class OnboardingFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = viewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel by viewModels<OnboardingViewModel>()
        return ComposeView(requireContext()).apply {
            setContent {
                DurexBankTheme {
                    OnboardingScreen(viewModel)
                }
            }
        }
    }
}
