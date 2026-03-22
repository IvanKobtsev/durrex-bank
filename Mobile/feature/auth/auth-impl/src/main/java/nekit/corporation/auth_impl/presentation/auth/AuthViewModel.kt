package nekit.corporation.auth_impl.presentation.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.auth.domain.Validator
import nekit.corporation.auth.domain.model.Credentials
import nekit.corporation.auth.domain.model.RegisterModel
import nekit.corporation.auth.domain.usecase.GetCredentialsUseCase
import nekit.corporation.auth.domain.usecase.LoginUseCase
import nekit.corporation.auth.domain.usecase.RegisterUseCase
import nekit.corporation.auth_impl.R
import nekit.corporation.auth_impl.navigation.AuthNavigator
import nekit.corporation.auth_impl.presentation.model.AuthEvent.ShowToast
import nekit.corporation.auth_impl.presentation.model.AuthState
import nekit.corporation.auth_impl.presentation.model.Field
import nekit.corporation.auth_impl.presentation.model.Password
import nekit.corporation.auth_impl.presentation.sign.`in`.SignInInteract
import nekit.corporation.auth_impl.presentation.sign.up.SignUpInteract
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.onboarding_shared.domain.usecase.GetSettingsUseCase
import nekit.corporation.util.domain.common.BadRequestFailure
import nekit.corporation.util.domain.common.CommonBackendFailure
import nekit.corporation.util.domain.common.CredentialsError
import nekit.corporation.util.domain.common.ForbiddenFailure
import nekit.corporation.util.domain.common.NoConnectionFailure
import nekit.corporation.util.domain.common.NotFoundFailure
import nekit.corporation.util.domain.common.ServerFailure
import nekit.corporation.util.domain.common.UnknownFailure
import nekit.corporation.utils.PhoneNumberUtils

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Inject
@ViewModelKey(AuthViewModel::class)
@ContributesIntoMap(
    AppScope::class,
    binding = binding<@ClassKey(AuthViewModel::class) SignUpInteract>()
)
class AuthViewModel(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val getCredentialsUseCase: GetCredentialsUseCase,
    private val accountRepository: AccountRepository,
    private val navigator: AuthNavigator,
) : StatefulViewModel<AuthState>(), SignUpInteract, SignInInteract {
    init {
        observeSignUpState()
        observeSignInState()
        viewModelScope.launch(Dispatchers.IO) {
            reduceError {
                val credentials = getCredentialsUseCase()
                try {
                    accountRepository.getAllAccounts()
                    if (credentials == null)
                        onSignInOpen()
                    else if (!getSettingsUseCase.execute().isShowedOnboarding)
                        navigator.onOnboardingOpen()
                    else
                        navigator.onMainOpen()
                } catch (_: Throwable) {
                    onSignInOpen()
                }

            }
        }
    }

    override fun createInitialState(): AuthState {
        return AuthState.Init
    }

    override fun onLoginChange(login: String) {
        if (currentScreenState is AuthState.SignUpState) {
            updateStateOf<AuthState.SignUpState> {
                if (!this.login.isObserverActive) {
                    observeSignUpLogin()
                }

                copy(login = this.login.copy(text = login, isObserverActive = true))
            }
        }

        if (currentScreenState is AuthState.SignInState) {
            updateStateOf<AuthState.SignInState> {
                if (!this.login.isObserverActive) {
                    observeSignInLogin()
                }
                copy(login = this.login.copy(text = login, isObserverActive = true))
            }
        }
    }

    override fun onEmailChange(email: String) {
        updateStateOf<AuthState.SignUpState> {
            if (!this.email.isObserverActive) {
                observeSignUpEmail()
            }
            copy(
                email = this.email.copy(
                    text = email,
                    isObserverActive = true
                )
            )
        }
    }

    override fun onFirstNameChange(firstName: String) {
        updateStateOf<AuthState.SignUpState> {
            if (!this.firstName.isObserverActive) {
                observeSignUpFirstName()
            }
            copy(
                firstName = this.firstName.copy(
                    text = firstName,
                    isObserverActive = true
                )
            )
        }
    }

    override fun onLastNameChange(lastName: String) {
        updateStateOf<AuthState.SignUpState> {
            if (!this.lastName.isObserverActive) {
                observeSignUpLastName()
            }
            copy(
                lastName = this.lastName.copy(
                    text = lastName,
                    isObserverActive = true
                )
            )
        }
    }

    override fun onPhoneChange(phone: String) {
        updateStateOf<AuthState.SignUpState> {
            if (!this.phone.isObserverActive) {
                observeSignUpPhone()
            }
            copy(
                phone = this.phone.copy(
                    text = PhoneNumberUtils.getFilteredPhone(phone),
                    isObserverActive = true
                )
            )
        }
    }

    override fun onPasswordChange(password: String) {
        if (currentScreenState is AuthState.SignUpState) {
            updateStateOf<AuthState.SignUpState> {
                if (!this.password.isObserverActive) {
                    observeSignUpPassword()
                }
                copy(password = this.password.copy(password = password, isObserverActive = true))
            }
        }
        if (currentScreenState is AuthState.SignInState) {
            updateStateOf<AuthState.SignInState> {
                if (!this.password.isObserverActive) {
                    observeSignInPassword()
                }
                copy(password = this.password.copy(password = password, isObserverActive = true))
            }
        }
    }

    override fun onPasswordIconClick() {
        if (currentScreenState is AuthState.SignUpState) {
            updateStateOf<AuthState.SignUpState> {
                copy(password = this.password.copy(isVisible = !this.password.isVisible))
            }
        }
        if (currentScreenState is AuthState.SignInState) {
            updateStateOf<AuthState.SignInState> {
                copy(password = this.password.copy(isVisible = !this.password.isVisible))
            }
        }
    }

    override fun onSignInClick() {
        val localState = currentScreenState
        viewModelScope.launch(Dispatchers.IO) {
            if (localState is AuthState.SignInState) {
                reduceError {
                    updateStateOf<AuthState.SignInState> {
                        copy(isLoading = true)
                    }
                    with(localState) {
                        loginUseCase.execute(
                            Credentials(
                                login = login.text,
                                password = password.password
                            )
                        )
                    }
                    navigator.onOnboardingOpen()
                }

                updateStateOf<AuthState.SignInState> {
                    copy(isLoading = false)
                }
            }
        }
    }

    override fun onRepeatPasswordChange(repeatPassword: String) {
        if (currentScreenState is AuthState.SignUpState) {
            updateStateOf<AuthState.SignUpState> {
                if (!this.repeatPassword.isObserverActive) {
                    observeSignUpRepeatPassword()
                }
                copy(
                    repeatPassword = this.repeatPassword.copy(
                        password = repeatPassword,
                        isObserverActive = true
                    )
                )
            }
        }
    }

    override fun onRepeatPasswordIconClick() {
        if (currentScreenState is AuthState.SignUpState) {
            updateStateOf<AuthState.SignUpState> {
                copy(password = this.repeatPassword.copy(isVisible = !this.repeatPassword.isVisible))
            }
        }
    }

    override fun onSignUpClick() {
        val localState = currentScreenState
        viewModelScope.launch(Dispatchers.IO) {
            if (localState is AuthState.SignUpState) {
                reduceError {
                    updateStateOf<AuthState.SignUpState> {
                        copy(isLoading = true)
                    }

                    with(localState) {
                        registerUseCase.execute(
                            RegisterModel(
                                login = login.text,
                                password = password.password,
                                email = email.text,
                                firstName = firstName.text,
                                lastName = lastName.text,
                                phone = phone.text
                            )
                        )
                    }
                    navigator.onOnboardingOpen()

                }

                updateStateOf<AuthState.SignUpState> {
                    copy(isLoading = false)
                }
            }
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
    }

    fun onSignInOpen() {
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
    }

    private fun observeSignUpPassword() = with(viewModelScope) {
        launch(Dispatchers.Default + SupervisorJob()) {
            screenState.map { it.currentState }
                .filterIsInstance<AuthState.SignUpState>()
                .distinctUntilChanged { lastValue, newValue ->
                    lastValue.password.password == newValue.password.password
                }
                .debounce(DELAY_BETWEEN_CHECKING)
                .mapLatest {
                    Validator.validateEmptyField(it.password.password)
                }
                .collect {
                    updateStateOf<AuthState.SignUpState> {
                        copy(
                            password = password.copy(error = reduceValidationError(it))
                        )
                    }
                    if (currentScreenState is AuthState.SignInState) {
                        cancel()
                    }
                }
        }
    }

    private fun observeSignUpPhone() = with(viewModelScope) {
        /*launch(Dispatchers.Default + SupervisorJob()) {
            screenState.map { it.currentState }
                .filterIsInstance<AuthState.SignUpState>()
                .distinctUntilChanged { lastValue, newValue ->
                    lastValue.phone.text == newValue.phone.text
                }
                .debounce(DELAY_BETWEEN_CHECKING)
                .mapLatest {
                    validator.phoneValidator(it.phone.text)
                }
                .collect {
                    updateStateOf<AuthState.SignUpState> {
                        copy(
                            phone = phone.copy(error = reduceValidationError(it))
                        )
                    }
                    if (currentScreenState is AuthState.SignInState) {
                        cancel()
                    }
                }
        }*/
    }

    private fun observeSignUpEmail() = with(viewModelScope) {
        launch(Dispatchers.Default + SupervisorJob()) {
            screenState.map { it.currentState }
                .filterIsInstance<AuthState.SignUpState>()
                .distinctUntilChanged { lastValue, newValue ->
                    lastValue.email.text == newValue.email.text
                }
                .debounce(DELAY_BETWEEN_CHECKING)
                .mapLatest {
                    Validator.emailValidator(it.email.text)
                }
                .collect {
                    updateStateOf<AuthState.SignUpState> {
                        copy(
                            email = email.copy(error = reduceValidationError(it))
                        )
                    }
                    if (currentScreenState is AuthState.SignInState) {
                        cancel()
                    }
                }
        }
    }

    private fun observeSignUpFirstName() = with(viewModelScope) {
        launch(Dispatchers.Default + SupervisorJob()) {
            screenState.map { it.currentState }
                .filterIsInstance<AuthState.SignUpState>()
                .distinctUntilChanged { lastValue, newValue ->
                    lastValue.firstName.text == newValue.firstName.text
                }
                .debounce(DELAY_BETWEEN_CHECKING)
                .mapLatest {
                    Validator.firstNameValidator(it.firstName.text)
                }
                .collect {
                    updateStateOf<AuthState.SignUpState> {
                        copy(
                            firstName = firstName.copy(error = reduceValidationError(it))
                        )
                    }
                    if (currentScreenState is AuthState.SignInState) {
                        cancel()
                    }
                }
        }
    }

    private fun observeSignUpLastName() = with(viewModelScope) {
        launch(Dispatchers.Default + SupervisorJob()) {
            screenState.map { it.currentState }
                .filterIsInstance<AuthState.SignUpState>()
                .distinctUntilChanged { lastValue, newValue ->
                    lastValue.lastName.text == newValue.lastName.text
                }
                .debounce(DELAY_BETWEEN_CHECKING)
                .mapLatest {
                    Validator.lastNameValidator(it.lastName.text)
                }
                .collect {
                    updateStateOf<AuthState.SignUpState> {
                        copy(
                            lastName = lastName.copy(error = reduceValidationError(it))
                        )
                    }
                    if (currentScreenState is AuthState.SignInState) {
                        cancel()
                    }
                }
        }
    }

    private fun observeSignUpRepeatPassword() = with(viewModelScope) {
        launch(Dispatchers.Default + SupervisorJob()) {
            screenState.map { it.currentState }
                .filterIsInstance<AuthState.SignUpState>()
                .distinctUntilChanged { lastValue, newValue ->
                    lastValue.repeatPassword.password == newValue.repeatPassword.password
                }
                .debounce(DELAY_BETWEEN_CHECKING)
                .mapLatest {
                    Validator.validateEmptyField(it.repeatPassword.password)
                        ?: Validator.validateRepeatPassword(
                            it.password.password,
                            it.repeatPassword.password
                        )
                }
                .collect {
                    updateStateOf<AuthState.SignUpState> {
                        copy(
                            repeatPassword = repeatPassword.copy(error = reduceValidationError(it))
                        )
                    }
                    if (currentScreenState is AuthState.SignInState) {
                        cancel()
                    }
                }
        }
    }

    private fun observeSignUpLogin() = with(viewModelScope) {
        launch(Dispatchers.Default + SupervisorJob()) {
            screenState.map { it.currentState }
                .filterIsInstance<AuthState.SignUpState>()
                .distinctUntilChanged { lastValue, newValue ->
                    lastValue.login.text == newValue.login.text
                }
                .debounce(DELAY_BETWEEN_CHECKING)
                .mapLatest {
                    Validator.validateEmptyField(it.login.text)
                        ?: Validator.validateLogin(it.login.text)
                }
                .collect {
                    updateStateOf<AuthState.SignUpState> {
                        copy(
                            login = login.copy(error = reduceValidationError(it))
                        )
                    }
                    if (currentScreenState is AuthState.SignInState) {
                        cancel()
                    }
                }
        }
    }

    private fun observeSignInLogin() = with(viewModelScope) {
        launch(Dispatchers.Default + SupervisorJob()) {
            screenState.map { it.currentState }
                .filterIsInstance<AuthState.SignInState>()
                .distinctUntilChanged { lastValue, newValue ->
                    lastValue.login.text == newValue.login.text
                }
                .debounce(DELAY_BETWEEN_CHECKING)
                .mapLatest {
                    Validator.validateEmptyField(it.login.text)
                        ?: Validator.validateLogin(it.login.text)
                }
                .collect {
                    updateStateOf<AuthState.SignInState> {
                        copy(
                            login = login.copy(error = reduceValidationError(it))
                        )
                    }
                    if (currentScreenState is AuthState.SignUpState) {
                        cancel()
                    }
                }
        }
    }

    private fun observeSignInPassword() = with(viewModelScope) {
        launch(Dispatchers.Default + SupervisorJob()) {
            screenState
                .map { it.currentState }
                .filterIsInstance<AuthState.SignInState>()
                .distinctUntilChanged { lastValue, newValue ->
                    lastValue.password.password == newValue.password.password
                }
                .debounce(DELAY_BETWEEN_CHECKING)
                .mapLatest {
                    Validator.validateEmptyField(it.password.password)
                }.collect {
                    updateStateOf<AuthState.SignInState> {
                        copy(
                            password = password.copy(error = reduceValidationError(it))
                        )
                    }
                    if (currentScreenState is AuthState.SignUpState) {
                        cancel()
                    }
                }
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
        const val DELAY_BETWEEN_CHECKING = 1000L
    }
}