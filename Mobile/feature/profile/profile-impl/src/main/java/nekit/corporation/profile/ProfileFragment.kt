package nekit.corporation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material3.R
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.launch
import nekit.corporation.ThemeViewModel
import nekit.corporation.common.FragmentKey
import nekit.corporation.profile.compose.ProfileContent
import nekit.corporation.profile.mvvm.UiEvents

@ContributesIntoMap(AppScope::class)
@FragmentKey(ProfileFragment::class)
@Inject
internal class ProfileFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = viewModelFactory
    private val themeViewModel: ThemeViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel by viewModels<ProfileViewModel>()
        lifecycleScope.launch {
            viewModel.screenEvents.collect {
                when (it as? UiEvents) {
                    is UiEvents.ShowToast -> {
                        Toast.makeText(
                            requireContext(),
                            getString((it as UiEvents.ShowToast).textRes),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    null -> Unit
                    UiEvents.ChangeTheme -> themeViewModel.toggleTheme()
                }
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                val state by viewModel.screenState.collectAsStateWithLifecycle()
                ProfileContent(state.currentState, viewModel)
            }
        }

    }
}