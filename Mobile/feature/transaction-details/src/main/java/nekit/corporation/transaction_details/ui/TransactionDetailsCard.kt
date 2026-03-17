package nekit.corporation.transaction_details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import nekit.corporation.loan_shared.domain.model.TransactionTypeDomain
import nekit.corporation.transaction_details.R
import nekit.corporation.ui.component.InfoRow
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun TransactionDetailsCard(
    id: Long,
    type: TransactionTypeDomain,
    amount: Double,
    balanceBefore: Double,
    balanceAfter: Double,
    description: String?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.bgPrimary)
            .padding(16.dp)
    ) {
        InfoRow(R.string.id, "$id")
        InfoRow(R.string.type, type.toString())
        InfoRow(R.string.amount, "$amount")
        InfoRow(R.string.balanceBefore, balanceBefore.toString())
        InfoRow(R.string.balanceAfter, balanceAfter.toString())
        description?.let { InfoRow(R.string.description, it) }
    }
}
