package nekit.corporation.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nekit.corporation.presentation.compose.TabItem
import nekit.corporation.presentation.model.BottomBarState
import nekit.corporation.presentation.model.MainBottomBarTabs
import nekit.corporation.shell_main.R
import nekit.corporation.ui.component.Caption
import nekit.corporation.ui.theme.DurexBankTheme
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun MainBottomBar(state: BottomBarState, onTabClick: (MainBottomBarTabs) -> Unit) {
    val colors = LocalAppColors.current

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.bgPrimary),
        containerColor = colors.bgPrimary
    ) {
        MainBottomBarTabs.entries.forEach { tab ->
            TabItem(
                isSelected = tab == state.selectedTab,
                tab = tab,
                onClick = onTabClick
            )
        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMainBottomBar() {
    DurexBankTheme {
        MainBottomBar(
            state = BottomBarState(MainBottomBarTabs.Main),
            onTabClick = { }
        )
    }
}