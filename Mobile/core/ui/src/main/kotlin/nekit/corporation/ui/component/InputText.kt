package nekit.corporation.ui.component

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nekit.corporation.ui.theme.DurexBankTheme
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.ui.theme.Typography

@Composable
fun SecondaryInputText(
    text: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
) {
    val colors = LocalAppColors.current
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        textStyle = Typography.bodySmall,
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = colors.permanentPrimary,
            focusedContainerColor = colors.bgTertiary,
            unfocusedTextColor = colors.fontInvert,
            focusedTextColor = colors.fontInvert,
            unfocusedContainerColor = colors.bgTertiary,
            focusedBorderColor = colors.bgTertiary,
        ),
        placeholder = {
            Body2Text(placeholder, color = colors.fontInvert)
        },
        modifier = modifier
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewInputText() {
    DurexBankTheme {
        SecondaryInputText(
            text = "",
            onValueChange = {},
            placeholder = "Inter descti"
        )
    }
}