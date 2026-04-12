package nekit.corporation.history_impl.presentation.menu

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
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import nekit.corporation.common.di.FragmentKey
import nekit.corporation.history_impl.presentation.menu.mvvm.HistoryEvent
import nekit.corporation.history_impl.ui.HistoryScreen
import nekit.corporation.ui.theme.DurexBankTheme

@ContributesIntoMap(AppScope::class)
@FragmentKey(HistoryFragment::class)
@Inject
class HistoryFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = viewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            val viewModel by viewModels<HistoryViewModel>()
            setContent {
                val state by viewModel.screenState.collectAsStateWithLifecycle()
                viewModel.screenEvents.CollectEvent {
                    when (it as? HistoryEvent) {
                        is HistoryEvent.ShowToast -> {
                            Toast.makeText(
                                requireContext(),
                                getString((it as HistoryEvent.ShowToast).res),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        null -> Unit
                    }
                }
                DurexBankTheme {
                    HistoryScreen(viewModel)
                }
            }
        }
    }
}