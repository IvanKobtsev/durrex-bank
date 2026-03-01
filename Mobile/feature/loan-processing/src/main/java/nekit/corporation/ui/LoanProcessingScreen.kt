package nekit.corporation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nekit.corporation.ui.component.BaseTitle
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.loan_processing.R
import nekit.corporation.presentation.model.LoanProcessingEvent
import nekit.corporation.presentation.processing.LoanProcessingViewModel
import nekit.corporation.ui.component.BasicButton
import nekit.corporation.ui.component.BasicInputField
import nekit.corporation.ui.component.BodyText
import nekit.corporation.ui.component.Caption
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.utils.PhoneNumberVisualTransformation

@Composable
fun LoanProcessingScreen(viewModel: LoanProcessingViewModel) {
    val colors = LocalAppColors.current
    val context = LocalContext.current
    val state = viewModel.screenState.collectAsStateWithLifecycle().value.currentState
    viewModel.screenEvents.CollectEvent {
        if (it is LoanProcessingEvent) {
            when (it) {
                is LoanProcessingEvent.ShowToast -> Toast.makeText(
                    context,
                    it.textRes,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colors.bgPrimary)
            .padding(
                top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
            ),
    ) {
        BaseTitle(
            onClick = viewModel::onBackClick,
            label = stringResource(R.string.loan_processing),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        BodyText(
            text = stringResource(R.string.your_data),
            color = colors.fontPrimary,
            modifier = Modifier
                .padding(horizontal = 16.dp)

        )
        BasicInputField(
            value = state.firstName.text,
            onValueChange = viewModel::onFirstNameChange,
            label = stringResource(R.string.first_name_placeholder),
            isError = state.firstName.error != null,
            supportingText = state.firstName.error?.let { stringResource(it) } ?: "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        BasicInputField(
            value = state.lastName.text,
            onValueChange = viewModel::onLastNameChange,
            label = stringResource(R.string.second_name_placeholder),
            isError = state.lastName.error != null,
            supportingText = state.lastName.error?.let { stringResource(it) } ?: "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        BasicInputField(
            value = state.phone.text,
            onValueChange = viewModel::onPhoneChange,
            label = stringResource(R.string.phone_placeholder),
            isError = state.phone.error != null,
            supportingText = state.phone.error?.let { stringResource(it) } ?: "",
            visualTransformation = PhoneNumberVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Phone
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Caption(
            string = stringResource(R.string.loan_processing_description),
            color = colors.fontSecondary,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )
        Spacer(Modifier.weight(1f))
        BasicButton(
            text = stringResource(R.string.create_loan),
            onClick = viewModel::onCreateClick,
            isEnable = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
        )
    }
}

@Preview
@Composable
private fun PreviewLoanProcessingScreen() {
    LoansAppTheme {
        // LoanProcessingScreen()
    }
}