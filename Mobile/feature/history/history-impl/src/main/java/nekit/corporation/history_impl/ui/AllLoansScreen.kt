package nekit.corporation.history_impl.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nekit.corporation.history_impl.R
import nekit.corporation.history_impl.presentation.all.loans.AllLoansViewModel
import nekit.corporation.history_impl.presentation.models.AllLoansState
import nekit.corporation.ui.component.BaseTitle
import nekit.corporation.ui.component.CreditRow
import nekit.corporation.ui.theme.DurexBankTheme

@Composable
internal fun AllLoansScreen(viewModel: AllLoansViewModel) {
    val state = viewModel.screenState.collectAsStateWithLifecycle().value.currentState
    if (state is AllLoansState.Component) {
        Column(
            Modifier.padding(
                top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
            )
        ) {
            BaseTitle(
                onClick = viewModel::onClose,
                label = stringResource(R.string.my_loans),
            )
            LazyColumn {
                items(state.loans) {
                    CreditRow(
                        creditId = it.id,
                        tariffName = it.tariffName,
                        amount = it.amount,
                        onClick = { viewModel.onOpenLoan(it) }
                    )
                }
            }
        }
    }

}

@Preview
@Composable
private fun PreviewAllLoansScreen() {
    DurexBankTheme {
        //AllLoansScreen()
    }
}