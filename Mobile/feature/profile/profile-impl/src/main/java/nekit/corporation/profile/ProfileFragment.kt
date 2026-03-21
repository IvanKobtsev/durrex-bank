package nekit.corporation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import nekit.corporation.common.FragmentKey
import nekit.corporation.profile.compose.ProfileContent

@ContributesIntoMap(AppScope::class)
@FragmentKey(ProfileFragment::class)
@Inject
internal class ProfileFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = viewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel by viewModels<ProfileViewModel>()
        return ComposeView(requireContext()).apply {
            setContent {
                val state by viewModel.screenState.collectAsStateWithLifecycle()
                ProfileContent(state.currentState, viewModel)
            }
        }
    }
}