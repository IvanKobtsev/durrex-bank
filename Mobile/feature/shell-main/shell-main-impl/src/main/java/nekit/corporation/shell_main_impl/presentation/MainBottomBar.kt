package nekit.corporation.shell_main_impl.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nekit.corporation.shell_main_impl.presentation.compose.TabItem
import nekit.corporation.shell_main_impl.presentation.model.BottomBarState
import nekit.corporation.shell_main_api.model.Tab
import nekit.corporation.ui.theme.DurexBankTheme
import nekit.corporation.ui.theme.LocalAppColors

@Composable
internal fun MainBottomBar(state: BottomBarState, onTabClick: (Tab) -> Unit) {
    val colors = LocalAppColors.current

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.bgPrimary),
        containerColor = colors.bgPrimary
    ) {
        Tab.entries.forEach { tab ->
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
            state = BottomBarState(Tab.Main),
            onTabClick = { }
        )
    }
}