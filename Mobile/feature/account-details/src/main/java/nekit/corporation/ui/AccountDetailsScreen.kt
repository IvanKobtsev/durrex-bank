@file:Suppress("DEPRECATION")

package nekit.corporation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
import nekit.corporation.account_details.R
import nekit.corporation.architecture.presentation.EventQueue
import nekit.corporation.loan_shared.data.datasource.remote.model.AccountStatus
import nekit.corporation.loan_shared.domain.model.Account
import nekit.corporation.loan_shared.domain.model.Transaction
import nekit.corporation.loan_shared.domain.model.TransactionTypeDomain
import nekit.corporation.presentation.model.AccountDetailsEvent
import nekit.corporation.presentation.model.AccountDetailsInteractions
import nekit.corporation.presentation.model.AccountDetailsState
import nekit.corporation.ui.component.BaseIconButton
import nekit.corporation.ui.component.BasicInputField
import nekit.corporation.ui.component.Body2Text
import nekit.corporation.ui.component.BodyText
import nekit.corporation.ui.component.Caption
import nekit.corporation.ui.component.Headline2
import nekit.corporation.ui.component.LoadingScreen
import nekit.corporation.ui.component.PrimaryButton
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.utils.NumberVisualTransformation
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
            if (state.isLoading) LoadingScreen(modifier = Modifier.fillMaxSize())
            else {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
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
                            stringResource(R.string.number) + " ${state.account.id}",
                            color = colors.fontPrimary
                        )
                        Spacer(Modifier.weight(1f))
                        if (state.isDeleteCan) {
                            BaseIconButton(
                                onClick = interactions::onDeleteClick,
                                modifier = Modifier
                                    .padding(16.dp),
                                iconRes = R.drawable.contract_delete

                            )
                        }
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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Caption(
                                        string = stringResource(R.string.state),
                                        color = colors.fontSecondary
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Body2Text(
                                        text = state.account.status.name,
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        color = colors.fontPrimary
                                    )
                                }
                            }
                            Spacer(Modifier.height(24.dp))
                        }
                        item {
                            BodyText(
                                stringResource(R.string.add_operation),
                                color = colors.fontPrimary
                            )
                            Spacer(Modifier.height(16.dp))
                        }
                        item {
                            Row(
                                modifier = Modifier,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BasicInputField(
                                    visualTransformation = NumberVisualTransformation(),
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    value = state.sum.toString(),
                                    onValueChange = interactions::onSumChange,
                                    label = stringResource(R.string.operation_sum),
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(Modifier.weight(1f))

                                Box {
                                    BodyText(
                                        text = state.selectedOperation.name,
                                        color = colors.fontPrimary,
                                        modifier = Modifier.clickable { interactions.onOpen() }
                                    )

                                    DropdownMenu(
                                        expanded = state.isOperationTypeOpen,
                                        onDismissRequest = interactions::onDismiss,
                                        containerColor = colors.bgPrimary
                                    ) {
                                        persistentListOf(
                                            TransactionTypeDomain.DEBIT,
                                            TransactionTypeDomain.DEPOSIT,
                                        ).forEach { currency ->
                                            DropdownMenuItem(
                                                text = {
                                                    BodyText(
                                                        currency.name,
                                                        color = colors.fontPrimary
                                                    )
                                                },
                                                onClick = {
                                                    interactions.onSelectOperation(currency)
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            PrimaryButton(
                                text = stringResource(R.string.apply),
                                onClick = interactions::onApplyClick,
                                isEnable = state.isApplyButtonEnable,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            if (state.transactions.isNotEmpty()) {
                                Spacer(Modifier.height(24.dp))
                                BodyText(
                                    stringResource(R.string.operations),
                                    color = colors.fontPrimary
                                )
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                        items(state.transactions) {
                            AccountOperation(it, interactions::onTransactionOpen)
                            Spacer(Modifier.height(8.dp))
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
                ),
                isApplyButtonEnable = true,
                sum = "0.0",
                selectedOperation = TransactionTypeDomain.DEBIT,
                isOperationTypeOpen = true,
                isLoading = false,
                transactions = persistentListOf(),
                isDeleteCan = false
            ),
            interactions = object : AccountDetailsInteractions {
                override fun onBack() {
                    TODO("Not yet implemented")
                }

                override fun onApplyClick() {
                    TODO("Not yet implemented")
                }

                override fun onSumChange(sum: String) {
                    TODO("Not yet implemented")
                }


                override fun onDeleteClick() {
                    TODO("Not yet implemented")
                }

                override fun onSelectOperation(operation: TransactionTypeDomain) {
                    TODO("Not yet implemented")
                }

                override fun onDismiss() {
                    TODO("Not yet implemented")
                }

                override fun onOpen() {
                    TODO("Not yet implemented")
                }

                override fun onTransactionOpen(transaction: Transaction) {
                    TODO("Not yet implemented")
                }

            }
        )
    }
}