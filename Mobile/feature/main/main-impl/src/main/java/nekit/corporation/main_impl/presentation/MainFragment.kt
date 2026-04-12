package nekit.corporation.main_impl.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import nekit.corporation.ThemeViewModel
import nekit.corporation.common.di.FragmentKey
import nekit.corporation.main_impl.ui.MainScreen
import nekit.corporation.ui.theme.DurexBankTheme

@ContributesIntoMap(AppScope::class)
@FragmentKey(MainFragment::class)
@Inject
class MainFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = viewModelFactory
    val viewModel by viewModels<MainViewModel>()
    val themeViewModel by activityViewModels<ThemeViewModel>()
    override fun onResume() {
        super.onResume()
        viewModel.init()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                DurexBankTheme {
                    val state by viewModel.screenState.collectAsStateWithLifecycle()
                    MainScreen(
                        state.currentState, viewModel.screenEvents, viewModel,
                        onThemeUpdate = { themeViewModel.setTheme(it) }
                    )
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.onNavigate()
    }
}