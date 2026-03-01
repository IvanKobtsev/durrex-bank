package nekit.corporation.auth.presentation.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
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
import nekit.corporation.auth.R
import nekit.corporation.auth.domain.Validator
import nekit.corporation.auth.domain.model.Credentials
import nekit.corporation.auth.domain.model.RegisterModel
import nekit.corporation.auth.domain.usecase.GetCredentialsUseCase
import nekit.corporation.auth.domain.usecase.LoginUseCase
import nekit.corporation.auth.domain.usecase.RegisterUseCase
import nekit.corporation.auth.presentation.model.AuthEvent
import nekit.corporation.auth.presentation.model.AuthState
import nekit.corporation.auth.presentation.model.Field
import nekit.corporation.auth.presentation.model.Password
import nekit.corporation.auth.navigation.AuthNavigation
import nekit.corporation.auth.presentation.sign.`in`.SignInInteract
import nekit.corporation.auth.presentation.sign.up.SignUpInteract
import nekit.corporation.onboarding_shared.domain.usecase.GetSettingsUseCase
import nekit.corporation.util.domain.common.BadRequestFailure
import nekit.corporation.util.domain.common.CommonBackendFailure
import nekit.corporation.util.domain.common.CredentialsError
import nekit.corporation.util.domain.common.NoConnectionFailure
import nekit.corporation.util.domain.common.NotFoundFailure
import nekit.corporation.util.domain.common.ServerFailure
import nekit.corporation.util.domain.common.UnknownFailure
import nekit.corporation.utils.PhoneNumberUtils
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class AuthViewModel @Inject constructor(
    private val validator: Validator,
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val getCredentialsUseCase: GetCredentialsUseCase,
    private val authNavigation: AuthNavigation
) : StatefulViewModel<AuthState>(), SignUpInteract, SignInInteract {
    init {
        observeSignUpState()
        observeSignInState()
        viewModelScope.launch(Dispatchers.IO) {
            reduceError {
                val credentials = getCredentialsUseCase()
                if (credentials == null)
                    onSignInOpen()
                else if (!getSettingsUseCase.execute().isShowedOnboarding)
                    authNavigation.openOnboarding()
                else
                    authNavigation.openMain()
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
                phone = this.email.copy(
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
                phone = this.firstName.copy(
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
                phone = this.lastName.copy(
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
                    authNavigation.openOnboarding()
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
                    authNavigation.openOnboarding()

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
                    offerEvent(AuthEvent.ShowToast(R.string.sign_up_bad_request))
                }

                is NoConnectionFailure ->
                    offerEvent(AuthEvent.ShowToast(R.string.not_connection_failure))

                is ServerFailure, is UnknownFailure ->
                    offerEvent(AuthEvent.ShowToast(R.string.strange_error))

            }
        } catch (e: CredentialsError) {
            when (e) {
                is CredentialsError.Cancelled ->
                    offerEvent(AuthEvent.ShowToast(R.string.cancelled_failure))

                is CredentialsError.Failure, is CredentialsError.NoCredentials ->
                    offerEvent(AuthEvent.ShowToast(R.string.strange_error))
            }
        } catch (e: Throwable) {
            offerEvent(AuthEvent.ShowToast(R.string.strange_error))
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
                    validator.validateEmptyField(it.password.password)
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
        launch(Dispatchers.Default + SupervisorJob()) {
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
        }
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
                    validator.emailValidator(it.email.text)
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
                    validator.firstNameValidator(it.firstName.text)
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
                    validator.lastNameValidator(it.lastName.text)
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
                    validator.validateEmptyField(it.repeatPassword.password)
                        ?: validator.validateRepeatPassword(
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
                    validator.validateEmptyField(it.login.text)
                        ?: validator.validateLogin(it.login.text)
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
                    validator.validateEmptyField(it.login.text)
                        ?: validator.validateLogin(it.login.text)
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
                    validator.validateEmptyField(it.password.password)
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