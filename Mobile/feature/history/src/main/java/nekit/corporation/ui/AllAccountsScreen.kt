package nekit.corporation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nekit.corporation.ui.component.BaseTitle
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.history.R
import nekit.corporation.presentation.all.accounts.AllAccountsViewModel
import nekit.corporation.presentation.models.AllAccountsState
import nekit.corporation.ui.component.AccountRow

@Composable
fun AllAccountsScreen(viewModel: AllAccountsViewModel) {
    val state = viewModel.screenState.collectAsStateWithLifecycle().value.currentState
    if (state is AllAccountsState.Component) {
        Column(
            Modifier.padding(
                top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
            )
        ) {
            BaseTitle(
                onClick = viewModel::onClose,
                label = stringResource(R.string.my_accounts),
            )
            LazyColumn {
                items(state.accounts) { ac ->
                    AccountRow(
                        account = ac.id,
                        amount = ac.balance,
                        onClick = { viewModel.onOpenAccount(it) }
                    )
                }
            }
        }
    }

}

@Preview
@Composable
private fun PreviewAllLoansScreen() {
    LoansAppTheme {
        //AllLoansScreen()
    }
}