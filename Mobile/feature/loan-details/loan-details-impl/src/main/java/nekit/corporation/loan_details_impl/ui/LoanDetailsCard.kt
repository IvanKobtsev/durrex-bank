package nekit.corporation.loan_details_impl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import nekit.corporation.loan_details_impl.R
import nekit.corporation.ui.component.InfoRow
import nekit.corporation.ui.theme.LocalAppColors

@Composable
internal fun LoanDetailsCard(
    loanNumber: Int,
    tariff: String,
    amount: Double,
    remainBalance: Double,
    nextPaymentDate: String,
    createAt: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.bgPrimary)
            .padding(16.dp)
    ) {
        InfoRow(R.string.loan_number, "${stringResource(R.string.number)} $loanNumber")
        InfoRow(R.string.tariff, tariff)
        InfoRow(R.string.create_at, createAt)
        InfoRow(R.string.next_payment, nextPaymentDate)
        InfoRow(R.string.remain_balance, remainBalance.toString())
        InfoRow(R.string.amount, "$amount")
    }
}
