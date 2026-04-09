package nekit.corporation.transaction_details_impl.presentation

import android.content.Context
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
import nekit.corporation.transaction_details_impl.ui.TransactionDetailsScreen
import nekit.corporation.ui.theme.DurexBankTheme

@ContributesIntoMap(AppScope::class)
@FragmentKey(TransactionDetailsFragment::class)
@Inject
class TransactionDetailsFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = viewModelFactory

    private val viewModel by viewModels<TransactionDetailsViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val accountId = requireArguments().getInt(ID_ARG_ACCOUNT)
        val transactionId = requireArguments().getLong(ID_ARG_TRANSACTION)
        viewModel.init(accountId, transactionId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                DurexBankTheme {
                    val state by viewModel.screenState.collectAsStateWithLifecycle()
                    TransactionDetailsScreen(state.currentState, viewModel, viewModel.screenEvents)
                }
            }
        }
    }

    companion object {
        const val ID_ARG_ACCOUNT = "account_id"
        const val ID_ARG_TRANSACTION = "transaction_id"
    }
}