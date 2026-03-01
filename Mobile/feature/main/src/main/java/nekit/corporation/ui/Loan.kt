package nekit.corporation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nekit.corporation.domain.Currency
import nekit.corporation.main.R
import nekit.corporation.ui.component.BasicButton
import nekit.corporation.ui.component.BodyText
import nekit.corporation.ui.component.Caption
import nekit.corporation.ui.component.Headline2
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.ui.theme.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Loan(
    onButtonClick: () -> Unit,
    expanded: Boolean,
    selectedCurrency: Currency,
    onItemSelect: (Currency) -> Unit,
    onDismiss: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors().copy(containerColor = colors.bgPrimary),
        modifier = modifier
    ) {
        Headline2(
            stringResource(R.string.create_account),
            colors.fontPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        HorizontalDivider(
            color = colors.bgSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Caption(
            string = stringResource(R.string.conditions),
            color = colors.fontSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        )

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            BodyText(
                text = stringResource(R.string.choose_currency),
                color = colors.fontPrimary,
            )
            Spacer(Modifier.weight(1f))

            Box {
                BodyText(
                    text = selectedCurrency.symbol,
                    color = colors.fontPrimary,
                    modifier = Modifier.clickable { onOpen() }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = onDismiss,
                    containerColor = colors.bgPrimary
                ) {
                    Currency.entries.forEach { currency ->
                        DropdownMenuItem(
                            text = { BodyText(currency.symbol) },
                            onClick = {
                                onItemSelect(currency)
                            },
                        )
                    }
                }
            }
        }

        BasicButton(
            text = stringResource(R.string.open_button),
            onClick = onButtonClick,
            isEnable = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewLoan() {
    LoansAppTheme {
        Loan(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onButtonClick = {},
            expanded = true,
            selectedCurrency = Currency.EUR,
            onItemSelect = { TODO() },
            onDismiss = { TODO() },
            onOpen = { TODO() }
        )
    }
}