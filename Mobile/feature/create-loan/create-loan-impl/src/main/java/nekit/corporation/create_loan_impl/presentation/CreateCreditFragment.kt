package nekit.corporation.create_loan_impl.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
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
import nekit.corporation.create_loan_impl.model.CreateLoanEvents
import nekit.corporation.ui.theme.DurexBankTheme

@Inject
@ContributesIntoMap(AppScope::class)
@FragmentKey(CreateCreditFragment::class)
class CreateCreditFragment(
    val viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = viewModelFactory

    val viewModel by viewModels<CreateLoanViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.screenEvents.collect {
                if (it is CreateLoanEvents) {
                    when (it) {
                        is CreateLoanEvents.ShowToast -> Toast.makeText(
                            requireContext(),
                            it.textRes,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {

                DurexBankTheme {
                    val state by viewModel.screenState.collectAsStateWithLifecycle()
                    CreateLoanScreen(state.currentState, viewModel)
                }
            }
        }
    }
}