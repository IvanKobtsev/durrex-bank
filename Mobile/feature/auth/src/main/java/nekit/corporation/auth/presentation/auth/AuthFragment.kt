package nekit.corporation.auth.presentation.auth

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import nekit.corporation.auth.databinding.FragmentAuthBinding
import nekit.corporation.auth.di.AuthFragmentInjector
import javax.inject.Inject

class AuthFragment : Fragment() {

    @Inject
    lateinit var viewModel: AuthViewModel

    private var binding: FragmentAuthBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as AuthFragmentInjector).inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        Log.d("RAG", "init_fragment")

        return binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.authScreen?.setContent {
            AuthScreen(viewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}