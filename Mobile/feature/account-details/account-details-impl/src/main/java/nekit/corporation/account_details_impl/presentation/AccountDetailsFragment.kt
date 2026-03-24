package nekit.corporation.account_details_impl.presentation

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
import nekit.corporation.account_details_impl.ui.AccountDetailsScreen
import nekit.corporation.common.FragmentKey
import nekit.corporation.ui.theme.DurexBankTheme
import kotlin.getValue

@Inject
@ContributesIntoMap(AppScope::class)
@FragmentKey(AccountDetailsFragment::class)
class AccountDetailsFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = viewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val id = requireArguments().getInt(ID_ARG)
        val viewModel by viewModels<AccountDetailsViewModel>()
        viewModel.init(id)
        return ComposeView(requireContext()).apply {
            setContent {
                DurexBankTheme {
                    val state by viewModel.screenState.collectAsStateWithLifecycle()
                    AccountDetailsScreen(
                        viewModel.screenEvents,
                        state.currentState,
                        viewModel
                    )
                }
            }
        }
    }

    companion object {
        const val ID_ARG = "account_details_id"
    }
}