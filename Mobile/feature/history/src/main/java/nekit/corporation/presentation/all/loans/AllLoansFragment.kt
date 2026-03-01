package nekit.corporation.presentation.all.loans

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import javax.inject.Inject
import nekit.corporation.di.AllLoansFragmentInjector
import nekit.corporation.history.databinding.MenuScreenBinding
import nekit.corporation.ui.AllLoansScreen
import nekit.corporation.ui.theme.LoansAppTheme

class AllLoansFragment : Fragment() {

    @Inject
    lateinit var viewModel: AllLoansViewModel

    private var binding: MenuScreenBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as AllLoansFragmentInjector).inject(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MenuScreenBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.menuScreen?.setContent {
            LoansAppTheme {
                AllLoansScreen(viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}