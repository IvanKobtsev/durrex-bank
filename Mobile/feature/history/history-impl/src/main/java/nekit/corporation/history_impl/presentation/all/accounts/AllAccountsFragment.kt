package nekit.corporation.history_impl.presentation.all.accounts

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
import nekit.corporation.common.FragmentKey
import nekit.corporation.history_impl.ui.AllAccountsScreen
import nekit.corporation.ui.theme.DurexBankTheme

@Suppress("DEPRECATION")
@ContributesIntoMap(AppScope::class)
@FragmentKey(AllAccountsFragment::class)
@Inject
class AllAccountsFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = viewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel by viewModels<AllAccountsViewModel>()
        viewModel.init()
        return ComposeView(requireContext()).apply {
            setContent {
                DurexBankTheme {
                    AllAccountsScreen(viewModel)
                }
            }
        }
    }
}