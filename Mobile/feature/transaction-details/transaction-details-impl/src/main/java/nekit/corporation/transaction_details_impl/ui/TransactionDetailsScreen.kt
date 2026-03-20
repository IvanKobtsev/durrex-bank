@file:Suppress("DEPRECATION")

package nekit.corporation.transaction_details.ui

import android.widget.Toast
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nekit.corporation.architecture.presentation.EventQueue
import nekit.corporation.loan_shared.domain.model.Transaction
import nekit.corporation.loan_shared.domain.model.TransactionTypeDomain
import nekit.corporation.transaction_details.R
import nekit.corporation.transaction_details.presentation.model.TransactionDetailsEvent
import nekit.corporation.transaction_details.presentation.model.TransactionDetailsState
import nekit.corporation.transaction_details.presentation.model.TransactionInteractions
import nekit.corporation.ui.component.BaseIconButton
import nekit.corporation.ui.component.Headline2
import nekit.corporation.ui.component.LoadingScreen
import nekit.corporation.ui.theme.DurexBankTheme
import nekit.corporation.ui.theme.LocalAppColors
import java.time.Instant

@Composable
fun TransactionDetailsScreen(
    state: TransactionDetailsState,
    interactions: TransactionInteractions,
    events: EventQueue
) {
    val colors = LocalAppColors.current
    val context = LocalContext.current

    events.CollectEvent {
        if (it is TransactionDetailsEvent) {
            when (it) {
                is TransactionDetailsEvent.ShowToast -> Toast.makeText(
                    context,
                    it.textRes,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    when (state) {
        TransactionDetailsState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
        is TransactionDetailsState.Content -> {
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
                        stringResource(R.string.transaction) + " ${state.transaction.id}",
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
                        TransactionDetailsCard(
                            id = state.transaction.id,
                            type = state.transaction.type,
                            amount = state.transaction.amount,
                            balanceBefore = state.transaction.balanceBefore,
                            balanceAfter = state.transaction.balanceAfter,
                            description = state.transaction.description,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewTransactionDetailsScreen() {
    DurexBankTheme {
        TransactionDetailsScreen(
            state = TransactionDetailsState.Content(
                transaction = Transaction(
                    id = 123L,
                    accountId = 132123,
                    type = TransactionTypeDomain.TRANSFER,
                    amount = 13213.0,
                    balanceBefore = 123123.0,
                    balanceAfter = 12313.0,
                    relatedAccountId = 123132,
                    description = "afsgsfgs",
                    createdAt = Instant.now()
                )
            ),
            interactions = object : TransactionInteractions {
                override fun onBack() {
                    TODO("Not yet implemented")
                }

            },
            events = EventQueue()
        )
    }
}