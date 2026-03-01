package nekit.corporation.presentation.state

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import nekit.corporation.di.LoanProcessingInjector
import nekit.corporation.di.LoanStateInjector
import nekit.corporation.loan_processing.databinding.LoanCreatingScreenBinding
import nekit.corporation.ui.LoanStateScreen
import javax.inject.Inject
import nekit.corporation.ui.theme.LoansAppTheme

class LoanStateFragment : Fragment() {

    @Inject
    lateinit var viewModel: LoanStateViewModel

    private var binding: LoanCreatingScreenBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as LoanStateInjector).inject(this)
        with(requireArguments()) {
            viewModel.init(
                amount = getInt(AMOUNT_ARG),
                context = context,
                period = getInt(PERIOD_ARG)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoanCreatingScreenBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.loanCreatingScreen?.setContent {
            LoansAppTheme {
                LoanStateScreen(viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val AMOUNT_ARG = "loan_amount_arg"
        const val PERIOD_ARG = "loan_period_arg"
        const val IS_APPROVED_ARG = "loan_is_approved_arg"
    }
}