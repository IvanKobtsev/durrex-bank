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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
import nekit.corporation.architecture.presentation.EventQueue
import nekit.corporation.domain.Currency
import nekit.corporation.ui.component.BaseTopBar
import nekit.corporation.main.R
import nekit.corporation.presentation.models.MainEvent
import nekit.corporation.presentation.models.MainState
import nekit.corporation.presentation.models.MainViewModelInteraction
import nekit.corporation.ui.component.Caption
import nekit.corporation.ui.component.Headline2
import nekit.corporation.ui.component.LoadingScreen
import nekit.corporation.ui.component.LoanBanner
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun MainScreen(state: MainState, eventQueue: EventQueue, interaction: MainViewModelInteraction) {
    val colors = LocalAppColors.current
    val context = LocalContext.current

    eventQueue.CollectEvent {
        if (it is MainEvent) {
            when (it) {
                is MainEvent.ShowToast -> Toast.makeText(
                    context,
                    it.textRes,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    if (state is MainState.Content) {
        Column(
            modifier = Modifier
                .background(colors.bgSecondary)
                .padding(top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding())
        ) {
            BaseTopBar(
                text = stringResource(R.string.main),
                onIconClick = interaction::openOnboarding
            )
            Spacer(Modifier.height(16.dp))
            LazyColumn {
                item {
                    LoanBanner(
                        Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
                item {
                    Loan(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onButtonClick = interaction::onAccountCreateClick,
                        expanded = state.isCurrencyMenuOpen,
                        selectedCurrency = state.selectedCurrency,
                        onItemSelect = interaction::onSelectCurrency,
                        onDismiss = interaction::onDismissCurrency,
                        onOpen = interaction::onOpenCurrencyMenu,
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
                item {
                    Headline2(
                        text = stringResource(R.string.my_loans),
                        color = colors.fontPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                item {
                    if (state.credits.isEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Caption(
                            string = stringResource(R.string.my_loans_placeholder),
                            color = colors.fontSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        CreditsCard(
                            loans = state.credits,
                            onButtonClick = interaction::onShowAllLoansClick,
                            onRowClick = interaction::onLoanClick
                        )
                    }
                }
                item {
                    Spacer(Modifier.height(16.dp))
                }
                item {
                    Headline2(
                        text = stringResource(R.string.my_accouts),
                        color = colors.fontPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                item {
                    if (state.credits.isEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Caption(
                            string = stringResource(R.string.my_accounts_placeholder),
                            color = colors.fontSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        CreditsCard(
                            loans = state.credits,
                            onButtonClick = interaction::onShowAllLoansClick,
                            onRowClick = interaction::onLoanClick
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    } else if (state is MainState.Loading) {
        LoadingScreen(modifier = Modifier.fillMaxSize())
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMainScreen() {
    LoansAppTheme {
        MainScreen(
            MainState.Content(
                loanSum = 200,
                isCurrencyMenuOpen = false,
                selectedCurrency = Currency.RUB,
                credits = persistentListOf(),
                accounts = persistentListOf()
            ),
            eventQueue = EventQueue(),
            interaction = object : MainViewModelInteraction {
                override fun openOnboarding() {
                    TODO("Not yet implemented")
                }

                override fun onCreateCreditClick() {
                    TODO("Not yet implemented")
                }

                override fun onAccountCreateClick() {
                    TODO("Not yet implemented")
                }

                override fun onShowAllLoansClick() {
                    TODO("Not yet implemented")
                }

                override fun onLoanClick(id: Int) {
                    TODO("Not yet implemented")
                }

                override fun onDismissCurrency() {
                    TODO("Not yet implemented")
                }

                override fun onSelectCurrency(currency: Currency) {
                    TODO("Not yet implemented")
                }

                override fun onOpenCurrencyMenu() {
                    TODO("Not yet implemented")
                }

            }
        )
    }
}
