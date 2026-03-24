@file:Suppress("DEPRECATION")

package nekit.corporation.loan_details_impl.ui

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
import nekit.corporation.architecture.presentation.EventQueue
import nekit.corporation.loan_details_impl.R
import nekit.corporation.loan_shared.domain.model.CreditDetail
import nekit.corporation.loan_shared.domain.model.CreditStatusDomain
import nekit.corporation.loan_shared.domain.model.PaymentScheduleEntry
import nekit.corporation.loan_details_impl.presentation.model.LoanDetailsEvent
import nekit.corporation.loan_details_impl.presentation.model.LoanDetailsState
import nekit.corporation.loan_details_impl.presentation.model.LoanInteractions
import nekit.corporation.ui.component.BaseIconButton
import nekit.corporation.ui.component.Body2Text
import nekit.corporation.ui.component.BodyText
import nekit.corporation.ui.component.Caption
import nekit.corporation.ui.component.Headline2
import nekit.corporation.ui.component.LoadingScreen
import nekit.corporation.ui.theme.DurexBankTheme
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.util.domain.common.toDate
import java.time.Instant

@Composable
internal fun LoanDetailsScreen(
    state: LoanDetailsState,
    interactions: LoanInteractions,
    events: EventQueue
) {
    val colors = LocalAppColors.current
    val context = LocalContext.current

    events.CollectEvent {
        if (it is LoanDetailsEvent) {
            when (it) {
                is LoanDetailsEvent.ShowToast -> Toast.makeText(
                    context,
                    it.textRes,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    when (state) {
        LoanDetailsState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
        is LoanDetailsState.Content -> {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        top = WindowInsets.systemBars.asPaddingValues()
                            .calculateTopPadding()
                    )
                ) {
                    BaseIconButton(
                        onClick = interactions::onBack,
                        modifier = Modifier
                            .padding(16.dp)

                    )
                    Spacer(Modifier.width(16.dp))
                    Headline2(
                        stringResource(R.string.number) + " ${state.credit.id}",
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
                        LoanDetailsCard(
                            loanNumber = state.credit.id,
                            tariff = state.credit.tariffName ?: "",
                            remainBalance = state.credit.remainingBalance,
                            amount = state.credit.amount,
                            nextPaymentDate = state.credit.nextPaymentDate?.toDate().toString(),
                            createAt = state.credit.issuedAt.toDate(),
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
                                    color = colors.fontPrimary
                                )
                                Spacer(Modifier.weight(1f))
                                Body2Text(
                                    text = state.credit.status.name,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                    item {
                        BodyText(stringResource(R.string.payments), color = colors.fontPrimary)
                        Spacer(Modifier.height(16.dp))
                    }
                    state.credit.schedule?.let {
                        items(it) {
                            PaymentCard(
                                id = it.id,
                                dueDate = it.dueDate.toDate(),
                                amount = it.amount,
                                isPaid = it.isPaid,
                                paidAt = it.paidAt?.toDate(),
                                modifier = Modifier
                            )
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewLoanDetailsScreen() {
    DurexBankTheme {
        LoanDetailsScreen(
            state = LoanDetailsState.Content(
                CreditDetail(
                    id = 123132,
                    clientId = 12345,
                    accountId = 132453,
                    tariffName = "afsgds",
                    amount = 123.23,
                    remainingBalance = 123.455,
                    status = CreditStatusDomain.ACTIVE,
                    issuedAt = Instant.now(),
                    nextPaymentDate = Instant.now(),
                    schedule = persistentListOf(
                        PaymentScheduleEntry(
                            id = 1214,
                            dueDate = Instant.now(),
                            amount = 124.44,
                            isPaid = true,
                            paidAt = Instant.now()
                        ),
                        PaymentScheduleEntry(
                            id = 1214,
                            dueDate = Instant.now(),
                            amount = 124.44,
                            isPaid = false,
                            paidAt = null
                        ),
                    )
                )
            ),
            interactions = object : LoanInteractions {
                override fun onBack() {
                    TODO("Not yet implemented")
                }

            },
            events = EventQueue()
        )
    }
}