package nekit.corporation.auth.presentation.model

import nekit.corporation.architecture.presentation.ScreenState

sealed interface AuthState : ScreenState {

    data object Init : AuthState

    data class SignUpState(
        val login: Field,
        val email: Field,
        val firstName: Field,
        val lastName: Field,
        val phone: Field,
        val password: Password,
        val repeatPassword: Password,
        val isSignUpButtonEnable: Boolean,
        val isLoading: Boolean
    ) : AuthState, ScreenState

    data class SignInState(
        val login: Field,
        val password: Password,
        val isSignInButtonEnable: Boolean,
        val isLoading: Boolean,
    ) : AuthState
}
