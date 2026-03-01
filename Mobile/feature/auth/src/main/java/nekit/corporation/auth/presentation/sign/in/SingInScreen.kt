package nekit.corporation.auth.presentation.sign.`in`

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nekit.corporation.auth.R
import nekit.corporation.auth.presentation.model.AuthState
import nekit.corporation.auth.presentation.model.Field
import nekit.corporation.auth.presentation.model.Password
import nekit.corporation.ui.component.BasicButton
import nekit.corporation.ui.component.BasicInputField
import nekit.corporation.ui.theme.LoansAppTheme

@Composable
fun SingInScreen(state: AuthState.SignInState, signInInteract: SignInInteract) {

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
    ) {
        BasicInputField(
            value = state.login.text,
            label = stringResource(R.string.login),
            onValueChange = signInInteract::onLoginChange,
            modifier = Modifier
                .fillMaxWidth(),
            isError = state.login.error != null,
            supportingText = state.login.error?.let { stringResource(it) } ?: "",
        )
        BasicInputField(
            value = state.password.password,
            label = stringResource(R.string.password),
            onValueChange = signInInteract::onPasswordChange,
            modifier = Modifier
                .fillMaxWidth(),
            icon = if (state.password.isVisible) R.drawable.eye else R.drawable.eye_cross,
            onIconClick = signInInteract::onPasswordIconClick,
            isError = state.password.error != null,
            supportingText = state.password.error?.let { stringResource(it) } ?: "",
            visualTransformation = if (state.password.isVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation()
        )
        BasicButton(
            text = stringResource(R.string.sign_up_action),
            onClick = signInInteract::onSignInClick,
            isEnable = state.isSignInButtonEnable,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSingInScreen() {
    LoansAppTheme {
        SingInScreen(
            AuthState.SignInState(
                login = Field(
                    "Iliy",
                    R.string.login,
                    false
                ),
                password = Password(
                    "Ponamorev",
                    null,
                    true,
                    false
                ),
                isSignInButtonEnable = true,
                isLoading = false
            ),
            object : SignInInteract {
                override fun onLoginChange(login: String) {
                    TODO("Not yet implemented")
                }

                override fun onPasswordChange(password: String) {
                    TODO("Not yet implemented")
                }

                override fun onPasswordIconClick() {
                    TODO("Not yet implemented")
                }

                override fun onSignInClick() {
                    TODO("Not yet implemented")
                }
            }
        )
    }
}
