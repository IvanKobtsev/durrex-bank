package nekit.corporation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.ui.R
import nekit.corporation.ui.theme.DurexBankTheme

@Composable
fun AccountDetailsCard(
    accountId: Int,
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
        InfoRow(R.string.account_number, "№ $accountId")
        InfoRow(R.string.balance, balance)
    }
}

@Preview
@Composable
private fun PreviewAccountDetailsCard() {
    DurexBankTheme {
        AccountDetailsCard(1, "safgdsf")
    }

}
