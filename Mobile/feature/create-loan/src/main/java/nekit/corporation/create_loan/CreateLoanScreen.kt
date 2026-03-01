package nekit.corporation.create_loan

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import nekit.corporation.create_loan.model.AccountUi
import nekit.corporation.create_loan.model.CreateLoanInteractions
import nekit.corporation.create_loan.model.CreateLoanState
import nekit.corporation.tariff.domain.model.Tariff
import nekit.corporation.ui.component.BaseIconButton
import nekit.corporation.ui.component.BasicButton
import nekit.corporation.ui.component.BasicInputField
import nekit.corporation.ui.component.BodyText
import nekit.corporation.ui.component.DropText
import nekit.corporation.ui.component.Headline
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.utils.NumberVisualTransformation

@Composable
fun CreateLoanScreen(state: CreateLoanState, interactions: CreateLoanInteractions) {
    val colors = LocalAppColors.current
    Column(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BaseIconButton(
                onClick = interactions::onBackClick,
                modifier = Modifier
                    .padding(
                        top = WindowInsets.systemBars.asPaddingValues()
                            .calculateTopPadding()
                    )
            )
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
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))
        state.selectedAccount?.let {
            DropText(
                selected = "id: ${it.id}\n${stringResource(R.string.balance)}: ${it.balance} ${it.currency}",
                variants = state.tariffs.map { tariff -> tariff.name ?: "" }.toImmutableList(),
                expanded = state.isAccountOpen,
                onExpandedChange = interactions::onExpandedTariffChange,
                onSelect = interactions::onSelectTariff,
                label = stringResource(R.string.select_account),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))
        BasicInputField(
            value = state.amount.toString(),
            onValueChange = interactions::onChangeAmount,
            label = stringResource(R.string.input_amount),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            visualTransformation = NumberVisualTransformation()
        )
        Spacer(Modifier.weight(1f))
        BasicButton(
            text = stringResource(R.string.create_loan),
            onClick = interactions::onCreateCredit,
            isEnable = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewCreateLoanScreen() {
    LoansAppTheme {
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
                isLoading = true
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

                override fun onSelectAccount(account: String) {
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