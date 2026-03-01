package nekit.corporation.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nekit.corporation.account_details.databinding.AccountDetailsScreenBinding
import nekit.corporation.di.AccountDetailsFragmentInjector
import nekit.corporation.ui.AccountDetailsScreen
import javax.inject.Inject
import nekit.corporation.ui.theme.LoansAppTheme

class AccountDetailsFragment : Fragment() {

    @Inject
    lateinit var viewModel: AccountDetailsViewModel

    private var binding: AccountDetailsScreenBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as AccountDetailsFragmentInjector).inject(this)
        val id = requireArguments().getInt(ID_ARG)
        viewModel.init(id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AccountDetailsScreenBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.accountDetailsScreen?.setContent {
            val state by viewModel.screenState.collectAsStateWithLifecycle()
            LoansAppTheme {
                AccountDetailsScreen(viewModel.screenEvents, state.currentState, viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val ID_ARG = "account_details_id"
    }
}