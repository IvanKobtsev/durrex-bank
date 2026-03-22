package nekit.corporation.transaction_impl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.launch
import nekit.corporation.common.FragmentKey
import nekit.corporation.transaction_impl.compose.TransactionContent
import nekit.corporation.transaction_impl.model.TransactionEvents
import nekit.corporation.ui.theme.DurexBankTheme

@ContributesIntoMap(AppScope::class)
@FragmentKey(TransactionFragment::class)
@Inject
class TransactionFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = viewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel by viewModels<TransactionViewModel>()
        lifecycleScope.launch {
            viewModel.screenEvents.collect {
                if (it is TransactionEvents) {
                    when (it) {
                        is TransactionEvents.ShowToast -> Toast.makeText(
                            requireContext(), it.textRes,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                DurexBankTheme() {
                    val state by viewModel.screenState.collectAsStateWithLifecycle()
                    TransactionContent(state.currentState, viewModel)
                }
            }
        }
    }
}