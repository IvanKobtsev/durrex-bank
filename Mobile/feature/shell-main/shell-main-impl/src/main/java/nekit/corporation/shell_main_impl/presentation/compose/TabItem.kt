package nekit.corporation.presentation.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import nekit.corporation.presentation.model.MainBottomBarTabs
import nekit.corporation.shell_main.R
import nekit.corporation.ui.component.Caption
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun RowScope.TabItem(
    isSelected: Boolean,
    tab: MainBottomBarTabs,
    onClick: (MainBottomBarTabs) -> Unit
) {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val color = if (isSelected)
            colors.permanentPrimaryDark else colors.fontSecondary

        IconButton(
            onClick = { onClick(tab) },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(tab.iconRes),
                contentDescription = "",
                tint = color
            )
        }
        Caption(
            string = stringResource(tab.nameRes),
            color = color
        )
    }
}