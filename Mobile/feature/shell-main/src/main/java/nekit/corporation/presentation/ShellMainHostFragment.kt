package nekit.corporation.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.androidx.AppNavigator
import nekit.corporation.di.MainBottomBarInjector
import nekit.corporation.navigation.MainBottomBarRouter
import nekit.corporation.navigation.Screens.main
import nekit.corporation.shell_main.R
import nekit.corporation.shell_main.databinding.ShellMainHostBinding
import nekit.corporation.ui.theme.LoansAppTheme
import javax.inject.Inject
import kotlin.getValue

class ShellMainHostFragment : Fragment() {

    @Inject
    lateinit var router: Cicerone<MainBottomBarRouter>
    private val childNavigator by lazy {
        AppNavigator(requireActivity(), R.id.tabs_container, childFragmentManager)
    }
    private var binding: ShellMainHostBinding? = null

    @Inject
    lateinit var viewModel: BottomBarViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainBottomBarInjector).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ShellMainHostBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.bottomBarCompose?.setContent {
            LoansAppTheme {
                MainBottomBar(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        router.getNavigatorHolder().setNavigator(childNavigator)
        if (childFragmentManager.findFragmentById(R.id.tabs_container) == null) {
            router.router.replaceScreen(main())
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
}
