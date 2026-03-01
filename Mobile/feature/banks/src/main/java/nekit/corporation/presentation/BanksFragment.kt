package nekit.corporation.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import nekit.corporation.BanksFragmentInjector
import nekit.corporation.banks.databinding.BankScreenBinding
import nekit.corporation.ui.BanksScreen
import javax.inject.Inject
import nekit.corporation.ui.theme.LoansAppTheme

@Suppress("DEPRECATION")
class BanksFragment : Fragment() {

    @Inject
    lateinit var viewModel: BanksViewModel

    private var binding: BankScreenBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as BanksFragmentInjector).inject(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BankScreenBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.banksScreen?.setContent {
            LoansAppTheme {
                BanksScreen(viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}