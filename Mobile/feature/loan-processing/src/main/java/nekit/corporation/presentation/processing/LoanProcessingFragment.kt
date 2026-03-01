package nekit.corporation.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import nekit.corporation.di.LoanProcessingInjector
import nekit.corporation.loan_processing.databinding.LoanCreatingScreenBinding
import nekit.corporation.presentation.processing.LoanProcessingViewModel
import nekit.corporation.ui.LoanProcessingScreen
import javax.inject.Inject
import nekit.corporation.ui.theme.LoansAppTheme

class LoanProcessingFragment : Fragment() {

    @Inject
    lateinit var viewModel: LoanProcessingViewModel

    private var binding: LoanCreatingScreenBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as LoanProcessingInjector).inject(this)
        with(requireArguments()) {
            viewModel.init(
                percent = getDouble(PERCENT_ARG),
                amount = getInt(AMOUNT_ARG),
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
                LoanProcessingScreen(viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val AMOUNT_ARG = "loan_processing_amount_arg"
        const val PERCENT_ARG = "loan_processing_percent_arg"
        const val PERIOD_ARG = "loan_processing_periodt_arg"
    }
}