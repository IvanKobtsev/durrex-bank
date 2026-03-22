package nekit.corporation.shell_main_impl.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import nekit.corporation.common.FragmentKey
import nekit.corporation.shell_main_api.MainShellApi
import nekit.corporation.shell_main_api.model.Tab
import nekit.corporation.shell_main_impl.R
import nekit.corporation.shell_main_impl.databinding.ShellMainHostBinding
import nekit.corporation.shell_main_impl.navigation.MainBottomBarNavigator
import nekit.corporation.shell_main_impl.navigation.MainBottomBarRouter
import nekit.corporation.ui.theme.DurexBankTheme
import kotlin.getValue

@ContributesIntoMap(
    AppScope::class,
    binding<@ClassKey(ShellMainHostFragment::class) MainShellApi>()
)
@FragmentKey(ShellMainHostFragment::class)
@Inject
internal class ShellMainHostFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment(), MainShellApi {

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = viewModelFactory

    @Inject
    private lateinit var router: Cicerone<MainBottomBarRouter>

    @Inject
    private lateinit var navigator: MainBottomBarNavigator
    private val childNavigator by lazy {
        AppNavigator(requireActivity(), R.id.tabs_container, childFragmentManager)
    }
    private var binding: ShellMainHostBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ShellMainHostBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel by viewModels<BottomBarViewModel>()
        binding?.bottomBarCompose?.setContent {
            DurexBankTheme {
                val state by viewModel.screenState.collectAsStateWithLifecycle()

                MainBottomBar(state.currentState, viewModel::onTabClick)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        router.getNavigatorHolder().setNavigator(childNavigator)
        if (childFragmentManager.findFragmentById(R.id.tabs_container) == null) {
            navigator.toTab(Tab.Main)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onPause() {
        router.getNavigatorHolder().removeNavigator()
        super.onPause()
    }

    override fun onTab(tab: Tab) = FragmentScreen {
        ShellMainHostFragment(viewModelFactory).also {
            navigator.toTab(tab)
        }
    }

    override fun back() {
        navigator.back()
    }

    override fun runScreen(screen: () -> Screen) {
        navigator.changeScreen(screen)
    }
}
