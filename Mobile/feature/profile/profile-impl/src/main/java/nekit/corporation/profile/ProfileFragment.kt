package nekit.corporation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nekit.corporation.profile.compose.ProfileContent
import javax.inject.Inject

internal class ProfileFragment : Fragment() {

    @Inject
    lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val state by viewModel.screenState.collectAsStateWithLifecycle()
                ProfileContent(state.currentState, viewModel)
            }
        }
    }
}