package nekit.corporation.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import nekit.corporation.di.LoanDetailsFragmentInjector
import nekit.corporation.loan_details.databinding.LoanDetailsScreenBinding
import nekit.corporation.ui.LoanDetailsScreen
import javax.inject.Inject
import nekit.corporation.ui.theme.LoansAppTheme

class LoanDetailsFragment : Fragment() {

    @Inject
    lateinit var viewModel: LoanDetailsViewModel

    private var binding: LoanDetailsScreenBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as LoanDetailsFragmentInjector).inject(this)
        val id = requireArguments().getInt(ID_ARG)
        viewModel.init(id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoanDetailsScreenBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.loanDetailsScreen?.setContent {
            LoansAppTheme {
                LoanDetailsScreen(viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val ID_ARG = "laon_details_id"
    }
}