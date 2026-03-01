package nekit.corporation.ui

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
import nekit.corporation.ui.component.BaseTopBar
import nekit.corporation.presentation.menu.HistoryViewModel
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.history.R

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val colors = LocalAppColors.current
    val state = viewModel.screenState.collectAsStateWithLifecycle().value.currentState

    Column(
        modifier = Modifier
            .background(colors.bgPrimary)
            .padding(top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding())
            .fillMaxSize()
    ) {
        BaseTopBar(
            text = stringResource(R.string.menu),
            onIconClick = viewModel::openOnboarding
        )
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(state.transactions) {
                TransactionsRow(it) {
                   // viewModel.on(it)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMenuScreen() {
    LoansAppTheme {
        //MenuScreen()
    }
}