package nekit.corporation.auth_impl.presentation.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.launch
import nekit.corporation.ThemeViewModel
import nekit.corporation.auth_impl.AuthManager
import nekit.corporation.auth_impl.presentation.model.AuthEvent
import nekit.corporation.common.di.FragmentKey
import nekit.corporation.ui.theme.DurexBankTheme
import kotlin.getValue

@ContributesIntoMap(AppScope::class)
@FragmentKey(AuthFragment::class)
@Inject
class AuthFragment(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val authManager: AuthManager,
) : Fragment() {

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = viewModelFactory


    private val viewModel by viewModels<AuthViewModel>()

    private val loginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { data ->
            authManager.handleAuthResponse(
                data,
                onSuccess = { accessToken, idToken, refreshToken ->
                    viewModel.onAuthCodeReceived(accessToken, idToken, refreshToken)
                },
                onError = { error ->
                    Toast.makeText(
                        requireContext(),
                        "Login failed: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        lifecycleScope.launch {
            authManager.initialize(
                onReady = {

                },
                onError = { error ->
                    Log.e(TAG, "Discovery failed", error)
                }
            )
            viewModel.screenEvents.collect {
                if (it is AuthEvent) {
                    when (it) {
                        is AuthEvent.OpenLogin -> loginLauncher.launch(it.intent)

                        is AuthEvent.ShowToast -> Toast.makeText(
                            requireContext(),
                            it.stringResId,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {

                DurexBankTheme {
                    AuthScreen(viewModel)
                }
            }
        }
    }

    private companion object{
        private const val TAG = "AuthFragment"
    }
}