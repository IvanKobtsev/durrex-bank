package nekit.corporation.ui

import androidx.compose.foundation.layout.Arrangement
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
import nekit.corporation.ui.component.Body2Text
import nekit.corporation.history.R
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.user.domain.model.Transaction
import nekit.corporation.user.domain.model.TransactionTypeDomain
import java.time.Instant

@Composable
fun TransactionsRow(transaction: Transaction, onIconClick: () -> Unit) {
    val colors = LocalAppColors.current

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Body2Text(text = transaction.id.toString(), color = colors.fontPrimary)
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
fun PreviewTransactionsRow() {
    LoansAppTheme {
        TransactionsRow(
            Transaction(
                id = 120,
                accountId = 123,
                type = TransactionTypeDomain.TRANSFER,
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