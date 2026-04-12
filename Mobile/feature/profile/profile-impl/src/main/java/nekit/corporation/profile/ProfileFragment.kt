package nekit.corporation.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import nekit.corporation.common.di.FragmentKey
import nekit.corporation.profile.compose.ProfileContent
import nekit.corporation.profile.mvvm.ProfileNavigation
import nekit.corporation.profile.mvvm.UiEvents
import nekit.corporation.ui.theme.DurexBankTheme

@ContributesIntoMap(AppScope::class)
@FragmentKey(ProfileFragment::class)
@Inject
class ProfileFragment(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val navigation: ProfileNavigation
) : Fragment() {

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = viewModelFactory

    private val logoutLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { data ->
            Log.d(TAG, "date: $data")
            navigation.toAuth()
        }
    }
    val viewModel by viewModels<ProfileViewModel>()
    private val themeViewModel: ThemeViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        lifecycleScope.launch {
            viewModel.screenEvents.collect {
                if (it is UiEvents) {
                    when (it) {
                        is UiEvents.ShowToast -> {
                            Toast.makeText(
                                requireContext(),
                                getString(it.textRes),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        UiEvents.ChangeTheme -> themeViewModel.toggleTheme()
                        is UiEvents.OnLogout -> logoutLauncher.launch(it.intent)
                    }
                }
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                DurexBankTheme {
                    val state by viewModel.screenState.collectAsStateWithLifecycle()
                    ProfileContent(state.currentState, viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.init()
    }

    companion object {
        private const val TAG = "ProfileFragment"
    }
}