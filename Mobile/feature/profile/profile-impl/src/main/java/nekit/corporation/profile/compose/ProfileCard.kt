package nekit.corporation.profile.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nekit.corporation.profile.model.AccountModel
import nekit.corporation.profile_impl.R
import nekit.corporation.ui.component.InfoRow
import nekit.corporation.ui.theme.DurexBankTheme
import nekit.corporation.ui.theme.LocalAppColors

@Composable
internal fun ProfileCard(accountModel: AccountModel, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    Column(
        modifier
            .background(color = colors.bgPrimary, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InfoRow(
            iconRes = R.drawable.user_attributes_ic,
            stringRes = R.string.first_name,
            text = accountModel.firstName,
        )
        InfoRow(
            iconRes = R.drawable.user_attributes_ic,
            stringRes = R.string.last_name,
            text = accountModel.lastName,
        )
        InfoRow(
            iconRes = R.drawable.email_ic,
            stringRes = R.string.email,
            text = accountModel.email,
        )
        InfoRow(
            iconRes = R.drawable.phone_ic,
            stringRes = R.string.phone,
            text = accountModel.phone,
        )
        InfoRow(
            iconRes = if (accountModel.isBlocked) R.drawable.lock_ic else R.drawable.lock_open_ic,
            stringRes = R.string.status,
            text = stringResource(if (accountModel.isBlocked) R.string.blocked else R.string.unblocked),
        )
        InfoRow(
            iconRes = R.drawable.star_ic,
            stringRes = R.string.credit_rating,
            text = accountModel.rating.toString(),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0XF1F1F2)
@Composable
private fun PreviewProfileCard() {
    DurexBankTheme {
        ProfileCard(
            accountModel = AccountModel(
                firstName = "Ivan",
                lastName = "Ivanov",
                email = "william.rufus.day@pet-store.com",
                phone = "+79999999999",
                isBlocked = false,
                rating = 5.0f
            )
        )
    }
}