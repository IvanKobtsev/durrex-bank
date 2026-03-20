package nekit.corporation.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nekit.corporation.ui.R
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun CreditRow(
    creditId: Int,
    tariffName: String?,
    amount: Double,
    modifier: Modifier = Modifier,
    onClick: ((Int) -> Unit)? = null
) {
    val colors = LocalAppColors.current

    Column(
        modifier
            .padding(vertical = 8.dp)
            .clickable(
                enabled = onClick != null,
                interactionSource = null
            ) {
                onClick?.invoke(creditId)
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            BodyText(
                text = stringResource(R.string.tariff) + " $tariffName",
                color = colors.fontPrimary,
            )
            Spacer(Modifier.weight(1f))
            BodyText(
                text = "$amount",
                color = colors.fontPrimary
            )
        }
    }
}

@Composable
fun InfoRow(
    @StringRes stringRes: Int,
    text: String,
    textColor: Color = LocalAppColors.current.fontPrimary
) {
    val colors = LocalAppColors.current
    Row(modifier = Modifier.fillMaxWidth()) {
        Caption(string = stringResource(stringRes), color = colors.fontSecondary)
        Spacer(Modifier.weight(1f))
        Body2Text(
            text = text,
            color = textColor,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Composable
fun InfoRow(
    @DrawableRes iconRes: Int,
    @StringRes stringRes: Int,
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = LocalAppColors.current.permanentPrimaryDark
) {
    val colors = LocalAppColors.current
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(ImageVector.vectorResource(iconRes), null)
        Spacer(Modifier.width(32.dp))
        Column() {
            Caption(string = stringResource(stringRes), color = colors.fontSecondary)
            BodyText(
                text = text,
                color = textColor,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

    }
}

@Composable
fun AccountRow(
    account: Int,
    amount: Double,
    modifier: Modifier = Modifier,
    onClick: ((Int) -> Unit)? = null
) {
    val colors = LocalAppColors.current

    Column(
        modifier
            .padding(vertical = 8.dp)
            .clickable(
                enabled = onClick != null,
                interactionSource = null
            ) {
                onClick?.invoke(account)
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            BodyText(
                text = stringResource(R.string.account) + " $account",
                color = colors.fontPrimary,
            )
            Spacer(Modifier.weight(1f))
            BodyText(
                text = "$amount",
                color = colors.fontPrimary
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewLoanRow() {
    InfoRow(
        iconRes = R.drawable.question_ic,
        stringRes = R.string.account,
        text = "bad son"
    )
}