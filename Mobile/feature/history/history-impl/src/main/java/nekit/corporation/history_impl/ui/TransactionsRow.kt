package nekit.corporation.history_impl.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nekit.corporation.history_impl.R
import nekit.corporation.ui.component.Body2Text
import nekit.corporation.loan_shared.domain.model.Transaction
import nekit.corporation.loan_shared.domain.model.TransactionTypeDomain
import nekit.corporation.ui.theme.DurexBankTheme
import nekit.corporation.ui.theme.LocalAppColors
import java.time.Instant

@Composable
internal fun TransactionsRow(transaction: Transaction, onIconClick: () -> Unit) {
    val colors = LocalAppColors.current

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable(onClick = onIconClick, indication = null, interactionSource = null),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column() {
            Body2Text(text = transaction.id.toString(), color = colors.fontPrimary)
            Body2Text(text = transaction.type.name, color = colors.fontPrimary)
        }
        Spacer(Modifier.weight(1f))
        IconButton(
            onClick = onIconClick,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.arrow_right_ic),
                contentDescription = null,
                tint = colors.fontPrimary
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewTransactionsRow() {
    DurexBankTheme {
        TransactionsRow(
            Transaction(
                id = 120,
                accountId = 123,
                type = TransactionTypeDomain.Transfer,
                amount = 400.0,
                balanceBefore = 300.00,
                balanceAfter = 400.00,
                relatedAccountId = 300,
                description = "houy",
                createdAt = Instant.now()
            )
        ) {

        }
    }
}