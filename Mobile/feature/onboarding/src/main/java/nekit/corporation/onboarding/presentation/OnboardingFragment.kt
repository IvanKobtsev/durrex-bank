package nekit.corporation.onboarding.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import nekit.corporation.onboarding.presentation.ui.OnboardingScreen
import nekit.corporation.onboarding.databinding.FragmentOnboardingBinding
import nekit.corporation.onboarding.presentation.di.OnboardingFragmentInjector
import nekit.corporation.ui.theme.LoansAppTheme
import javax.inject.Inject

class OnboardingFragment : Fragment() {

    @Inject
    lateinit var viewModel: OnboardingViewModel

    private var binding: FragmentOnboardingBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as OnboardingFragmentInjector).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.onboardingScreen?.setContent {
            LoansAppTheme {
                OnboardingScreen(viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
