package nekit.corporation.create_loan

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nekit.corporation.create_loan.databinding.CreateCreditScreenBinding
import nekit.corporation.create_loan.navigation.CreateCreditInjector
import nekit.corporation.ui.theme.LoansAppTheme
import javax.inject.Inject

class CreateCreditFragment : Fragment() {

    @Inject
    lateinit var viewModel: CreateLoanViewModel


    private var binding: CreateCreditScreenBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as CreateCreditInjector).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CreateCreditScreenBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.createLoanScreen?.setContent {
            LoansAppTheme {
                val state by viewModel.screenState.collectAsStateWithLifecycle()
                CreateLoanScreen(state.currentState, viewModel)
            }
        }
    }

}