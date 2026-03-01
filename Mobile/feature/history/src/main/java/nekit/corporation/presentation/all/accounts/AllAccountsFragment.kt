package nekit.corporation.presentation.all.accounts

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import nekit.corporation.di.AllAccountsFragmentInjector
import nekit.corporation.history.databinding.MenuScreenBinding
import nekit.corporation.ui.AllAccountsScreen
import nekit.corporation.ui.theme.LoansAppTheme
import javax.inject.Inject

@Suppress("DEPRECATION")
class AllAccountsFragment : Fragment() {

    @Inject
    lateinit var viewModel: AllAccountsViewModel

    private var binding: MenuScreenBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as AllAccountsFragmentInjector).inject(this)

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
                AllAccountsScreen(viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}