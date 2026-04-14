package nekit.corporation.auth_impl.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.auth.domain.model.TokenDto
import nekit.corporation.auth.domain.repository.AuthRepository
import nekit.corporation.auth.domain.usecase.GetCredentialsUseCase
import nekit.corporation.auth_impl.AuthManager
import nekit.corporation.auth_impl.R
import nekit.corporation.auth_impl.navigation.AuthNavigator
import nekit.corporation.auth_impl.presentation.model.AuthEvent
import nekit.corporation.auth_impl.presentation.model.AuthEvent.ShowToast
import nekit.corporation.auth_impl.presentation.model.AuthState
import nekit.corporation.auth_impl.presentation.model.Field
import nekit.corporation.auth_impl.presentation.model.Password
import nekit.corporation.auth_impl.presentation.sign.`in`.SignInInteract
import nekit.corporation.auth_impl.presentation.sign.up.SignUpInteract
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.onboarding_shared.domain.usecase.GetSettingsUseCase
import nekit.corporation.push.domain.repository.PushRepository
import nekit.corporation.util.domain.common.BadRequestFailure
import nekit.corporation.util.domain.common.CommonBackendFailure
import nekit.corporation.util.domain.common.CredentialsError
import nekit.corporation.util.domain.common.ForbiddenFailure
import nekit.corporation.util.domain.common.NoConnectionFailure
import nekit.corporation.util.domain.common.NotFoundFailure
import nekit.corporation.util.domain.common.ServerFailure
import nekit.corporation.util.domain.common.UnknownFailure

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Inject
@ViewModelKey(AuthViewModel::class)
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
class AuthViewModel(
    private val oidcAuthManager: AuthManager,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val getCredentialsUseCase: GetCredentialsUseCase,
    private val accountRepository: AccountRepository,
    private val navigator: AuthNavigator,
    private val authRepository: AuthRepository,
    private val pushRepository: PushRepository,
) : StatefulViewModel<AuthState>(), SignUpInteract, SignInInteract {
    init {
        observeSignUpState()
        observeSignInState()

        viewModelScope.launch(Dispatchers.IO) {
            reduceError({
                val credentials = async {
                    try {
                        getCredentialsUseCase()
                    } catch (_: Throwable) {
                        onSignInOpen()
                    }
                }
                try {
                    accountRepository.getAllAccounts()
                    if (credentials.await() == null)
                        onSignInOpen()
                    else if (!getSettingsUseCase.execute().isShowedOnboarding) {
                        pushRepository.sendPushToken().onFailure {
                            Log.d(TAG,"e: $it")
                            navigator.onOnboardingOpen()
                        }.onSuccess {
                            Log.d(TAG,"s: $it")
                            navigator.onOnboardingOpen()
                        }
                    }
                    else {
                        pushRepository.sendPushToken().onFailure {
                            Log.d(TAG,"e: $it")
                            navigator.onMainOpen()
                        }.onSuccess {
                            Log.d(TAG,"s: $it")
                            navigator.onMainOpen()
                        }
                    }
                } catch (_: Throwable) {
                    onSignInOpen()
                }

            })
        }
    }

    override fun createInitialState(): AuthState {
        return AuthState.Init
    }

    override fun onSignInClick() {
        val intent = oidcAuthManager.buildLoginIntent()
        if (intent != null) {
            screenEvents.offerEvent(AuthEvent.OpenLogin(intent))
        } else {
            offerEvent(ShowToast(R.string.strange_error))
        }
    }

    fun onAuthCodeReceived(accessToken: String, idToken: String?, refreshToken: String?) {
        viewModelScope.launch {
            try {

                authRepository.saveToken(
                    TokenDto(
                        token = accessToken,
                        expiresAt = java.time.Instant.now().toString()
                    )
                )
                if (!getSettingsUseCase.execute().isShowedOnboarding)
                    navigator.onOnboardingOpen()
                else
                    navigator.onMainOpen()
            } catch (_: Exception) {
                offerEvent(ShowToast(R.string.strange_error))
            }
        }
    }

    override fun onSignUpClick() {
        val intent = oidcAuthManager.buildLoginIntent()
        if (intent != null) {
            screenEvents.offerEvent(AuthEvent.OpenLogin(intent))
        } else {
            offerEvent(ShowToast(R.string.strange_error))
        }
    }

    private suspend fun reduceError(block: suspend () -> Unit) {
        try {
            block()
        } catch (e: CommonBackendFailure) {
            when (e) {
                is BadRequestFailure, is NotFoundFailure -> {
                    if (currentScreenState is AuthState.SignUpState) {
                        updateStateOf<AuthState.SignUpState> {
                            copy(
                                isSignUpButtonEnable = false
                            )
                        }
                    }
                    if (currentScreenState is AuthState.SignInState) {
                        updateStateOf<AuthState.SignInState> {
                            copy(
                                isSignInButtonEnable = false
                            )
                        }
                    }
                    offerEvent(ShowToast(R.string.sign_up_bad_request))
                }

                is NoConnectionFailure ->
                    offerEvent(ShowToast(R.string.not_connection_failure))

                is ServerFailure, is UnknownFailure, is ForbiddenFailure ->
                    offerEvent(ShowToast(R.string.strange_error))
            }
        } catch (e: CredentialsError) {
            when (e) {
                is CredentialsError.Cancelled ->
                    offerEvent(ShowToast(R.string.cancelled_failure))

                is CredentialsError.Failure, is CredentialsError.NoCredentials ->
                    offerEvent(ShowToast(R.string.strange_error))
            }
        } catch (e: Throwable) {
            offerEvent(ShowToast(R.string.strange_error))
            Log.d(TAG, e.message.toString())
        }
    }

    fun onSignUpOpen() {
        updateState {
            AuthState.SignUpState(
                login = Field(text = "", error = null, false),
                password = Password(
                    password = "",
                    error = null,
                    isVisible = true,
                    isObserverActive = false
                ),
                repeatPassword = Password(
                    password = "",
                    error = null,
                    isVisible = true,
                    isObserverActive = false
                ),
                isSignUpButtonEnable = false,
                isLoading = false,
                email = Field(text = "", error = null, false),
                firstName = Field(text = "", error = null, false),
                lastName = Field(text = "", error = null, false),
                phone = Field(text = "", error = null, false)
            )
        }
        viewModelScope.launch(Dispatchers.Default) {
            observeSignUpState()
        }
    }

    fun onSignInOpen() {
        Log.d("RAG", "opem")
        updateState {
            AuthState.SignInState(
                login = Field(text = "", error = null, false),
                password = Password(
                    password = "",
                    error = null,
                    isVisible = true,
                    isObserverActive = false
                ),
                isSignInButtonEnable = false,
                isLoading = false
            )
        }
        viewModelScope.launch(Dispatchers.Default) {
            observeSignInState()
        }
    }


    private fun observeSignUpState() = with(viewModelScope) {
        launch(Dispatchers.Default + SupervisorJob()) {
            screenState.map { it.currentState }
                .filterIsInstance<AuthState.SignUpState>()
                .distinctUntilChanged { lastValue, newValue ->
                    lastValue.repeatPassword.error == newValue.repeatPassword.error &&
                            lastValue.login.error == newValue.login.error &&
                            lastValue.password.error == newValue.password.error
                }.collect {
                    updateStateOf<AuthState.SignUpState> {
                        copy(
                            isSignUpButtonEnable = it.login.error == null &&
                                    it.repeatPassword.error == null &&
                                    it.password.error == null
                        )
                    }
                    if (currentScreenState is AuthState.SignInState) {
                        cancel()
                    }
                }
        }
    }


    private fun observeSignInState() = with(viewModelScope) {
        launch(Dispatchers.Default + SupervisorJob()) {
            screenState.map { it.currentState }
                .filterIsInstance<AuthState.SignInState>()
                .distinctUntilChanged { lastValue, newValue ->
                    lastValue.login.error == newValue.login.error &&
                            lastValue.password.error == newValue.password.error
                }
                .collect {
                    updateStateOf<AuthState.SignInState> {
                        copy(
                            isSignInButtonEnable = it.login.error == null &&
                                    it.password.error == null
                        )
                    }
                    if (currentScreenState is AuthState.SignUpState) {
                        cancel()
                    }
                }
        }
    }

    private companion object {

        const val TAG = "AuthViewModel"
    }
}