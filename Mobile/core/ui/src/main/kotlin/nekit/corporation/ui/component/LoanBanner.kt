package nekit.corporation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nekit.corporation.ui.R
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun LoanBanner(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = colors.permanentPrimary,
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Headline2(stringResource(R.string.get_loan), color = colors.fontPrimary)
                Spacer(Modifier.height(12.dp))
                Body2Text(stringResource(R.string.get_loan_description_card), color = colors.fontPrimary)
            }
            Spacer(
                Modifier
                    .weight(1f)
            )
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.get_loans_backgrond_ic),
                contentDescription = null,
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewLoanBanner() {
    LoansAppTheme {
        LoanBanner(
            Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )
    }
}
