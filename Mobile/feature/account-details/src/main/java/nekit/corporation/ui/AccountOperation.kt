package nekit.corporation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nekit.corporation.account_details.R
import nekit.corporation.loan_shared.domain.model.Transaction
import nekit.corporation.ui.component.InfoRow
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun AccountOperation(
    transaction: Transaction,
    onClick: (Transaction) -> Unit,
) {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = { onClick(transaction) },
            )
            .background(colors.bgPrimary, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),

        ) {
        InfoRow(
            stringRes = R.string.id,
            text = transaction.id.toString(),
            textColor = colors.fontPrimary
        )
        InfoRow(
            stringRes = R.string.amount,
            text = transaction.amount.toString(),
            textColor = colors.fontPrimary
        )
        InfoRow(
            stringRes = R.string.type,
            text = transaction.type.toString(),
            textColor = colors.fontPrimary
        )
    }
}
