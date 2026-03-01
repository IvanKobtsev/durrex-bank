package nekit.corporation.auth.presentation.sign.up

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
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
import nekit.corporation.utils.PhoneNumberVisualTransformation

@Composable
fun SingUpScreen(state: AuthState.SignUpState, signUpInteract: SignUpInteract) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
    ) {
        item {
            BasicInputField(
                value = state.login.text,
                label = stringResource(R.string.login),
                onValueChange = signUpInteract::onLoginChange,
                modifier = Modifier
                    .fillMaxWidth(),
                isError = state.login.error != null,
                supportingText = state.login.error?.let { stringResource(it) } ?: "",
            )
        }

        item {
            BasicInputField(
                value = state.email.text,
                label = stringResource(R.string.email),
                onValueChange = signUpInteract::onLoginChange,
                modifier = Modifier
                    .fillMaxWidth(),
                isError = state.email.error != null,
                supportingText = state.email.error?.let { stringResource(it) } ?: "",
            )
        }
        item {
            BasicInputField(
                value = state.firstName.text,
                label = stringResource(R.string.first_name),
                onValueChange = signUpInteract::onLoginChange,
                modifier = Modifier
                    .fillMaxWidth(),
                isError = state.firstName.error != null,
                supportingText = state.firstName.error?.let { stringResource(it) } ?: "",
            )
        }
        item {
            BasicInputField(
                value = state.lastName.text,
                label = stringResource(R.string.last_name),
                onValueChange = signUpInteract::onLoginChange,
                modifier = Modifier
                    .fillMaxWidth(),
                isError = state.lastName.error != null,
                supportingText = state.lastName.error?.let { stringResource(it) } ?: "",
            )
        }
        item {
            BasicInputField(
                value = state.phone.text,
                label = stringResource(R.string.phone),
                onValueChange = signUpInteract::onLoginChange,
                modifier = Modifier
                    .fillMaxWidth(),
                isError = state.phone.error != null,
                supportingText = state.phone.error?.let { stringResource(it) } ?: "",
                visualTransformation = PhoneNumberVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone
                )
            )
        }

        item {
            BasicInputField(
                value = state.password.password,
                label = stringResource(R.string.password),
                onValueChange = signUpInteract::onPasswordChange,
                modifier = Modifier
                    .fillMaxWidth(),
                icon = if (state.password.isVisible) R.drawable.eye else R.drawable.eye_cross,
                onIconClick = signUpInteract::onPasswordIconClick,
                isError = state.password.error != null,
                supportingText = state.password.error?.let { stringResource(it) } ?: "",
                visualTransformation = if (state.password.isVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation()
            )
        }
        item {
            BasicInputField(
                value = state.repeatPassword.password,
                label = stringResource(R.string.repeat_password),
                onValueChange = signUpInteract::onRepeatPasswordChange,
                modifier = Modifier
                    .fillMaxWidth(),
                icon = if (state.repeatPassword.isVisible) R.drawable.eye else R.drawable.eye_cross,
                onIconClick = signUpInteract::onRepeatPasswordIconClick,
                isError = state.repeatPassword.error != null,
                supportingText = state.repeatPassword.error?.let { stringResource(it) } ?: "",
                visualTransformation = if (state.repeatPassword.isVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation()
            )
        }
        item {
            BasicButton(
                text = stringResource(R.string.sign_up_action),
                onClick = signUpInteract::onSignUpClick,
                isEnable = state.isSignUpButtonEnable,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
            )
        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSingInScreen() {
    LoansAppTheme {
        SingUpScreen(
            AuthState.SignUpState(
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
                repeatPassword = Password(
                    "Ponamorev1",
                    R.string.login,
                    false,
                    false
                ),
                isSignUpButtonEnable = true,
                isLoading = false,
                email = Field(
                    "Iliy",
                    R.string.login,
                    false
                ),
                firstName = Field(
                    "Iliy",
                    R.string.login,
                    false
                ),
                lastName = Field(
                    "Iliy",
                    R.string.login,
                    false
                ),
                phone = Field(
                    "Iliy",
                    R.string.login,
                    false
                )
            ),
            object : SignUpInteract {
                override fun onLoginChange(login: String) {
                    TODO("Not yet implemented")
                }

                override fun onEmailChange(email: String) {
                    TODO("Not yet implemented")
                }

                override fun onFirstNameChange(firstName: String) {
                    TODO("Not yet implemented")
                }

                override fun onLastNameChange(lastName: String) {
                    TODO("Not yet implemented")
                }

                override fun onPhoneChange(phone: String) {
                    TODO("Not yet implemented")
                }

                override fun onPasswordChange(password: String) {
                    TODO("Not yet implemented")
                }

                override fun onPasswordIconClick() {
                    TODO("Not yet implemented")
                }

                override fun onRepeatPasswordChange(repeatPassword: String) {
                    TODO("Not yet implemented")
                }

                override fun onRepeatPasswordIconClick() {
                    TODO("Not yet implemented")
                }

                override fun onSignUpClick() {
                    TODO("Not yet implemented")
                }

            }
        )
    }
}