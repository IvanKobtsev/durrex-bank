package nekit.corporation.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import nekit.corporation.ui.R
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun BaseTitle(onClick: () -> Unit, label: String, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClick, modifier = Modifier
                .padding(16.dp)
                .size(24.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.arrow_left_ic),
                contentDescription = null,
                tint = colors.iconSecondary
            )
        }
        Spacer(Modifier.width(16.dp))
        Headline2(
            label,
            color = colors.fontPrimary
        )
    }
}