package nekit.corporation.create_loan_impl.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import nekit.corporation.create_loan_impl.R
import nekit.corporation.create_loan_impl.model.AccountUi
import nekit.corporation.create_loan_impl.model.CreateLoanInteractions
import nekit.corporation.create_loan_impl.model.CreateLoanState
import nekit.corporation.tariff.domain.model.Tariff
import nekit.corporation.ui.component.BaseIconButton
import nekit.corporation.ui.component.PrimaryButton
import nekit.corporation.ui.component.PrimaryInputField
import nekit.corporation.ui.component.BodyText
import nekit.corporation.ui.component.DropText
import nekit.corporation.ui.component.Headline
import nekit.corporation.ui.component.LoadingScreen
import nekit.corporation.ui.theme.DurexBankTheme
import nekit.corporation.ui.theme.LocalAppColors

@Composable
internal fun CreateLoanScreen(state: CreateLoanState, interactions: CreateLoanInteractions) {
    val colors = LocalAppColors.current
    Box(
        Modifier
            .padding(horizontal = 16.dp)
            .padding(WindowInsets.systemBars.asPaddingValues())
            .fillMaxSize()
    ) {
        Column() {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BaseIconButton(onClick = interactions::onBackClick,)
                Spacer(Modifier.width(16.dp))
                Headline(stringResource(R.string.create_loan))
            }

            Spacer(Modifier.height(32.dp))
            BodyText(
                text = stringResource(R.string.process_date),
                color = colors.fontPrimary,
                modifier = Modifier.fillMaxWidth()

            )
            Spacer(Modifier.height(24.dp))
            state.selectedTariff?.let {
                DropText(
                    selected = state.selectedTariff.name ?: "",
                    variants = state.tariffs.map { it.name ?: "" }.toImmutableList(),
                    expanded = state.isTariffOpen,
                    onExpandedChange = interactions::onExpandedTariffChange,
                    onSelect = interactions::onSelectTariff,
                    label = stringResource(R.string.select_tariff),
                    getDisplayText = { state.selectedTariff.name ?: "" },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))
            val balance = stringResource(R.string.balance)
            state.selectedAccount?.let {
                DropText(
                    selected = it,
                    variants = state.accounts,
                    expanded = state.isAccountOpen,
                    onExpandedChange = interactions::onExpandedAccountChange,
                    onSelect = interactions::onSelectAccount,
                    label = stringResource(R.string.select_account),
                    modifier = Modifier.fillMaxWidth(),
                    getDisplayText = { account ->
                        "id: ${account.id}\n" +
                                "${balance}: ${account.balance} ${account.currency}"
                    },
                )
            }

            Spacer(Modifier.height(16.dp))
            PrimaryInputField(
                value = state.amount.toString(),
                onValueChange = interactions::onChangeAmount,
                label = stringResource(R.string.input_amount),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
            )
            Spacer(Modifier.weight(1f))
            PrimaryButton(
                text = stringResource(R.string.create_loan),
                onClick = interactions::onCreateCredit,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }
        if (state.isFatalError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Headline(stringResource(R.string.load_fail), color = colors.fontPrimary)
            }
        } else if (state.isLoading) {
            LoadingScreen(modifier = Modifier.fillMaxSize())
        }

    }


}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewCreateLoanScreen() {
    DurexBankTheme {
        CreateLoanScreen(
            state = CreateLoanState(
                selectedAccount = AccountUi(
                    id = 0,
                    balance = 30.0,
                    currency = "Rub"
                ),
                selectedTariff = Tariff(
                    id = 0,
                    name = "alpha",
                    interestRate = 2.0
                ),
                tariffs = persistentListOf(
                    Tariff(
                        id = 0,
                        name = "alpha",
                        interestRate = 2.0
                    ),
                    Tariff(
                        id = 0,
                        name = "alpha",
                        interestRate = 2.0
                    ),
                ),
                isTariffOpen = false,
                isAccountOpen = true,
                accounts = persistentListOf(
                    AccountUi(
                        id = 0,
                        balance = 30.0,
                        currency = "Rub"
                    ),
                    AccountUi(
                        id = 0,
                        balance = 30.0,
                        currency = "Rub"
                    ),
                ),
                amount = 24.0,
                isLoading = true,
                isFatalError = false,
            ),
            interactions = object : CreateLoanInteractions {
                override fun onBackClick() {
                    TODO("Not yet implemented")
                }

                override fun onSelectTariff(tariff: String) {
                    TODO("Not yet implemented")
                }

                override fun onExpandedTariffChange(isOpen: Boolean) {
                    TODO("Not yet implemented")
                }

                override fun onExpandedAccountChange(isOpen: Boolean) {
                    TODO("Not yet implemented")
                }

                override fun onSelectAccount(account: AccountUi) {
                    TODO("Not yet implemented")
                }

                override fun onCreateCredit() {
                    TODO("Not yet implemented")
                }

                override fun onChangeAmount(amount: String) {
                    TODO("Not yet implemented")
                }
            }
        )
    }
}