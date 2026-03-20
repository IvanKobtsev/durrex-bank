package nekit.corporation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import nekit.corporation.loan_details.R
import nekit.corporation.ui.component.InfoRow
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun PaymentCard(
    id: Int,
    dueDate: String,
    amount: Double,
    isPaid: Boolean,
    paidAt: String?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.bgPrimary)
            .padding(16.dp)
    ) {
        InfoRow(R.string.payment_number, "${stringResource(R.string.number)} $id")
        InfoRow(R.string.due_date, dueDate)

        InfoRow(
            R.string.state,
            stringResource(if (isPaid) R.string.paid else R.string.is_not_paid),
            textColor = if (isPaid) colors.indicatorPositive else colors.indicatorAttention
        )

        paidAt?.let {
            InfoRow(R.string.remain_balance, it)
        }
        InfoRow(R.string.amount, "$amount")
    }
}