package nekit.corporation.transaction_details.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nekit.corporation.transaction_details.di.TransactionDetailsFragmentInjector
import nekit.corporation.transaction_details.databinding.TransactionDetailsScreenBinding
import nekit.corporation.transaction_details.ui.TransactionDetailsScreen
import javax.inject.Inject
import nekit.corporation.ui.theme.LoansAppTheme

class TransactionDetailsFragment : Fragment() {

    @Inject
    lateinit var viewModel: TransactionDetailsViewModel

    private var binding: TransactionDetailsScreenBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as TransactionDetailsFragmentInjector).inject(this)
        val accountId = requireArguments().getInt(ID_ARG_ACCOUNT)
        val transactionId = requireArguments().getLong(ID_ARG_TRANSACTION)
        viewModel.init(accountId, transactionId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TransactionDetailsScreenBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.transactionDetailsScreen?.setContent {
            LoansAppTheme {
                val state by viewModel.screenState.collectAsStateWithLifecycle()
                TransactionDetailsScreen(state.currentState, viewModel, viewModel.screenEvents)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val ID_ARG_ACCOUNT = "account_id"
        const val ID_ARG_TRANSACTION = "transaction_id"
    }
}