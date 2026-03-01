package nekit.corporation.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.tooling.preview.Preview
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    Box(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { }
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = colors.permanentPrimary,
            trackColor = Transparent
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun PreviewLoadingScreen() {
    LoansAppTheme {
        LoadingScreen(modifier = Modifier.fillMaxSize())
    }
}
