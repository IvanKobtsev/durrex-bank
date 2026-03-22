package nekit.corporation.history_impl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nekit.corporation.history_impl.R
import nekit.corporation.ui.component.BaseTopBar
import nekit.corporation.history_impl.presentation.menu.HistoryViewModel
import nekit.corporation.ui.theme.DurexBankTheme
import nekit.corporation.ui.theme.LocalAppColors

@Composable
internal fun HistoryScreen(viewModel: HistoryViewModel) {
    val colors = LocalAppColors.current
    val state = viewModel.screenState.collectAsStateWithLifecycle().value.currentState

    Column(
        modifier = Modifier
            .background(colors.bgPrimary)
            .padding(top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding())
            .fillMaxSize()
    ) {
        BaseTopBar(
            text = stringResource(R.string.history),
            onIconClick = viewModel::openOnboarding
        )
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(state.transactions) {
                TransactionsRow(it) {
                    viewModel.onTransactionClick(it.id)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMenuScreen() {
    DurexBankTheme {
        //MenuScreen()
    }
}