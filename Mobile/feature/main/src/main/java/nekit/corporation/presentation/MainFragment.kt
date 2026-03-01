package nekit.corporation.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nekit.corporation.main.databinding.MainScreenBinding
import nekit.corporation.di.MainFragmentInjector
import nekit.corporation.ui.MainScreen
import nekit.corporation.ui.theme.LoansAppTheme
import javax.inject.Inject

class MainFragment : Fragment(){

    @Inject
    lateinit var viewModel: MainViewModel

    private var binding: MainScreenBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainFragmentInjector).inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainScreenBinding.inflate(inflater, container, false)
        return binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.mainScreen?.setContent {
            LoansAppTheme {
                val state by viewModel.screenState.collectAsStateWithLifecycle()
                MainScreen(state.currentState,viewModel.screenEvents,viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}