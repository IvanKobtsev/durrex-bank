package nekit.corporation.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.ui.theme.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseDialog(
    text: String,
    dismissText: String,
    accessText: String,
    onDismissClick: () -> Unit,
    onAccessClick: () -> Unit
) {
    val colors = LocalAppColors.current

    AlertDialog(
        onDismissRequest = onDismissClick,
        confirmButton = {
            TextButton(onClick = onAccessClick) {
                Body2Text(text = accessText, color = colors.permanentPrimaryDark)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissClick) {
                Body2Text(text = dismissText, color = colors.permanentPrimaryDark)
            }
        },
        title = {
            Headline(text = text, color = colors.fontPrimary)
        },
        containerColor = colors.bgPrimary,
        shape = RectangleShape
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewBaseDialog() {
    LoansAppTheme {
        Box(Modifier.fillMaxSize()) {
            BaseDialog(
                text = "Вы уверены, что хотите выйти?",
                dismissText = "Отмена",
                accessText = "Выйти",
                onDismissClick = {},
                onAccessClick = {}
            )
        }

    }
}