package nekit.corporation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nekit.corporation.presentation.model.LoanStateState
import nekit.corporation.presentation.state.LoanStateViewModel
import nekit.corporation.ui.component.StubScreen
import nekit.corporation.loan_processing.R

@Composable
fun LoanStateScreen(viewModel: LoanStateViewModel) {
    val state = viewModel.screenState.collectAsStateWithLifecycle().value.currentState

    when (state) {
        is LoanStateState.Approved -> StubScreen(
            label = stringResource(R.string.loan_label_approved, state.amount),
            description = stringResource(R.string.loan_description_approved, state.period),
            buttonText = stringResource(R.string.show_banks),
            onButtonClick = viewModel::onButtonClick,
            onCloseClick = viewModel::onBackClick,
            imageRes = R.drawable.success_img,
            isFullScreenDrawable = false,
        )

        LoanStateState.Init -> Unit
        LoanStateState.Rejected -> StubScreen(
            label = stringResource(R.string.loan_label_rejected),
            description = stringResource(R.string.loan_description_rejected),
            buttonText = stringResource(R.string.go_to_main),
            onButtonClick = viewModel::onButtonClick,
            onCloseClick = viewModel::onBackClick,
            imageRes = R.drawable.some_error_img,
            isFullScreenDrawable = false,
        )
    }
}