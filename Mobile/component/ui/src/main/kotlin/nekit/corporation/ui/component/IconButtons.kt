package nekit.corporation.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import nekit.corporation.ui.R
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun BaseIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int = R.drawable.cross_ic,
) {
    val colors = LocalAppColors.current
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = null,
            tint = colors.iconSecondary
        )
    }

}