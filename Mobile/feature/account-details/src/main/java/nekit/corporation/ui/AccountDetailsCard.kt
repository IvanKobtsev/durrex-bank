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
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.account_details.R
import nekit.corporation.ui.component.InfoRow

@Composable
fun AccountDetailsCard(
    loanNumber: Int,
    balance: String,
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
        InfoRow(R.string.balance, balance)
    }
}
