package nekit.corporation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nekit.corporation.ui.R
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun BaseTopBar(text: String, onIconClick: () -> Unit) {
    val colors = LocalAppColors.current

    Row(
        modifier = Modifier
            .background(colors.bgPrimary)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Headline2(
            text = text,
            color = colors.fontPrimary
        )
        Spacer(Modifier.weight(1f))
        IconButton(
            onClick = onIconClick,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .size(24.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.question_ic),
                contentDescription = "",
                modifier = Modifier,
                tint = colors.iconSecondary
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMainTopBar() {
    LoansAppTheme {
        BaseTopBar("Main") {

        }
    }
}