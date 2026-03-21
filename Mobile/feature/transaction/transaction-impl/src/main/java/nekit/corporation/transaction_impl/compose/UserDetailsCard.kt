package nekit.corporation.transaction.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import nekit.corporation.transaction_impl.R
import nekit.corporation.ui.component.InfoRow
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun UserDetailsCard(
    firstName: String,
    lastName: String,
    phone: String,
    modifier: Modifier,
) {
    val colors = LocalAppColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.bgPrimary)
            .padding(16.dp)
    ) {
        InfoRow(R.string.first_name, firstName)
        InfoRow(R.string.second_name, lastName)
        InfoRow(R.string.phone, phone)
    }
}