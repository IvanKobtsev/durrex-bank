package nekit.corporation.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun EnableButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = colors.bgSecondary,
            contentColor = colors.fontPrimary,
            disabledContainerColor = colors.bgSecondary,
            disabledContentColor = colors.fontPrimary
        ),
    ) {
        Body2Text(text)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewEnableButton() {
    EnableButton(
        "Зарегистрироваться", {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}