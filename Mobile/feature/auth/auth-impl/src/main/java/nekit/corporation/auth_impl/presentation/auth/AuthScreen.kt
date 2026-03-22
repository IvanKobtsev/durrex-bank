package nekit.corporation.auth_impl.presentation.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nekit.corporation.auth_impl.R
import nekit.corporation.auth_impl.presentation.model.AuthEvent
import nekit.corporation.auth_impl.presentation.model.AuthState
import nekit.corporation.auth_impl.presentation.sign.`in`.SignInInteract
import nekit.corporation.auth_impl.presentation.sign.`in`.SingInScreen
import nekit.corporation.auth_impl.presentation.sign.up.SignUpInteract
import nekit.corporation.auth_impl.presentation.sign.up.SingUpScreen
import nekit.corporation.ui.component.Body2Text
import nekit.corporation.ui.component.LoadingScreen
import nekit.corporation.ui.component.LogoText
import nekit.corporation.ui.theme.DurexBankTheme
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val colors = LocalAppColors.current

    val state = viewModel.screenState.collectAsStateWithLifecycle().value.currentState
    val context = LocalContext.current

    viewModel.screenEvents.CollectEvent {
        if (it is AuthEvent) {
           /* when (it) {
                is AuthEvent.ShowToast -> Toast.makeText(
                    context,
                    it.stringResId,
                    Toast.LENGTH_SHORT
                ).show()
            }*/
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(colors.fontPrimary),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    Modifier
                        .fillMaxHeight()
                        .offset(x = 8.dp)
                ) {
                    LogoText(stringResource(R.string.durex), colors.fontInvert)
                    LogoText(stringResource(R.string.bank), colors.fontInvert)
                }

                Spacer(Modifier.width(16.dp))
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.logo),
                    contentDescription = "",
                )
            }
            if (state is AuthState.Init) {
                LoadingScreen(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .background(colors.bgInvert.copy(alpha = 0.5f))
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AnimatedVisibility(
                visible = state is AuthState.SignUpState || state is AuthState.SignInState,
                enter = slideInVertically(),
                exit = slideOutVertically(),
            ) {
                AuthBottomSheet(
                    state = state,
                    signUpInteract = viewModel,
                    signInInteract = viewModel,
                    onSignUpClick = viewModel::onSignUpOpen,
                    onSignInClick = viewModel::onSignInOpen
                )
            }
        }
    }
}

@Composable
internal fun AuthBottomSheet(
    state: AuthState,
    signUpInteract: SignUpInteract,
    signInInteract: SignInInteract,
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .background(colors.fontPrimary)
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(colors.bgPrimary)
                .verticalScroll(rememberScrollState())

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Body2Text(
                    text = stringResource(R.string.sign_in),
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 17.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) { onSignInClick() },
                    textAlign = TextAlign.Center,
                    color = if (state is AuthState.SignInState)
                        colors.permanentPrimary
                    else
                        colors.fontSecondary,
                )
                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(0.61f),
                    thickness = 2.dp,
                    color = colors.bgDisable
                )
                Body2Text(
                    text = stringResource(R.string.sign_up),
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 17.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) { onSignUpClick() },
                    textAlign = TextAlign.Center,
                    color = if (state is AuthState.SignUpState)
                        colors.permanentPrimary
                    else
                        colors.fontSecondary
                )
            }
            if (state is AuthState.SignInState) {
                SingInScreen(state, signInInteract)

            }
            if (state is AuthState.SignUpState) {
                SingUpScreen(state, signUpInteract)
            }
        }
        if (state is AuthState.SignInState && state.isLoading ||
            state is AuthState.SignUpState && state.isLoading
        ) {
            LoadingScreen(
                Modifier
                    .background(colors.bgInvert.copy(alpha = 0.5f))
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp))
            )
        }
    }
}

@Preview
@Composable
fun PreviewAuthScreen() {
    DurexBankTheme {
        //AuthScreen()
    }
}