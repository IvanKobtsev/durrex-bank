@file:Suppress("DEPRECATION")

package nekit.corporation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nekit.corporation.presentation.model.AccountDetailsState
import nekit.corporation.ui.component.BaseIconButton
import nekit.corporation.ui.component.Caption
import nekit.corporation.ui.component.Headline2
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.models.LoanUiState
import nekit.corporation.presentation.AccountDetailsViewModel
import nekit.corporation.presentation.model.AccountDetailsEvent
import nekit.corporation.ui.component.Body2Text
import nekit.corporation.ui.component.LoadingScreen
import nekit.corporation.utils.getName
import nekit.corporation.account_details.R
import nekit.corporation.architecture.presentation.EventQueue
import nekit.corporation.loan_shared.data.datasource.remote.model.AccountStatus
import nekit.corporation.loan_shared.domain.model.Account
import nekit.corporation.presentation.model.AccountDetailsInteractions
import java.time.OffsetDateTime

@Composable
fun AccountDetailsScreen(
    screenEvents: EventQueue,
    state: AccountDetailsState,
    interactions: AccountDetailsInteractions
) {
    val colors = LocalAppColors.current
    val context = LocalContext.current

    screenEvents.CollectEvent {
        if (it is AccountDetailsEvent) {
            when (it) {
                is AccountDetailsEvent.ShowToast -> Toast.makeText(
                    context,
                    it.textRes,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    when (state) {
        AccountDetailsState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
        is AccountDetailsState.Content -> {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BaseIconButton(
                        onClick = interactions::onBack,
                        modifier = Modifier
                            .padding(16.dp)
                            .padding(
                                top = WindowInsets.systemBars.asPaddingValues()
                                    .calculateTopPadding()
                            )
                    )
                    Spacer(Modifier.width(16.dp))
                    Headline2(
                        stringResource(R.string.number) + " ${state.account.id}",
                        color = colors.fontPrimary
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        AccountDetailsCard(
                            loanNumber = state.account.id,
                            balance = "${state.account.balance} ${state.account.currency}",
                        )
                    }
                    item {
                        Spacer(Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(colors.bgPrimary)
                                .padding(16.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Caption(
                                    string = stringResource(R.string.state),
                                    color = colors.fontSecondary
                                )
                                Spacer(Modifier.weight(1f))
                                Body2Text(
                                    text = state.account.status.name,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }

            }
        }
    }
}


@Preview
@Composable
private fun PreviewLoanDetailsScreen() {
    LoansAppTheme {
        AccountDetailsScreen(
            screenEvents = EventQueue(),
            state = AccountDetailsState.Content(
                account = Account(
                    id = 12,
                    ownerId = 123,
                    balance = 132.0,
                    currency = "Rub",
                    status = AccountStatus.Open,
                    createdAt = OffsetDateTime.now(),
                    closedAt = OffsetDateTime.now()
                )
            ),
            interactions = object : AccountDetailsInteractions {
                override fun onBack() {
                    TODO("Not yet implemented")
                }

                override fun onDebitClick() {
                    TODO("Not yet implemented")
                }

                override fun onDepositClick() {
                    TODO("Not yet implemented")
                }

                override fun onDepositSumChange(deposit: String) {
                    TODO("Not yet implemented")
                }

                override fun onDebiSumChange(debit: String) {
                    TODO("Not yet implemented")
                }

            }
        )
    }
}