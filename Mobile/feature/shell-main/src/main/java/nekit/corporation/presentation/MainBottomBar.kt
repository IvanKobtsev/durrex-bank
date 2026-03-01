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
import nekit.corporation.presentation.model.MainBottomBarTabs
import nekit.corporation.shell_main.R
import nekit.corporation.ui.component.Caption
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun MainBottomBar(viewModel: BottomBarViewModel) {
    val state = viewModel.screenState.collectAsStateWithLifecycle().value.currentState
    val colors = LocalAppColors.current

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.bgPrimary),
        containerColor = colors.bgPrimary
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val color = if (state.selectedTab is MainBottomBarTabs.Main)
                colors.permanentPrimaryDark else colors.fontSecondary

            IconButton(
                onClick = { viewModel.onTabClick(MainBottomBarTabs.Main) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.home_ic),
                    contentDescription = "",
                    tint = color
                )
            }
            Caption(
                string = stringResource(R.string.main),
                color = color
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val color = if (state.selectedTab is MainBottomBarTabs.Menu)
                colors.permanentPrimaryDark else colors.fontSecondary

            IconButton(
                onClick = { viewModel.onTabClick(MainBottomBarTabs.Menu) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.menu_ic),
                    contentDescription = "",
                    tint = color
                )
            }
            Caption(
                string = stringResource(R.string.menu),
                color = color
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMainBottomBar() {
    LoansAppTheme {
        // MainBottomBar()
    }
}