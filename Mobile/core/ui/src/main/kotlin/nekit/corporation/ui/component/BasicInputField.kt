package nekit.corporation.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nekit.corporation.ui.R
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.ui.theme.Typography

@Composable
fun BasicInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    iconDescription: String? = null,
    onIconClick: () -> Unit = {},
    isError: Boolean = false,
    readOnly: Boolean = false,
    supportingText: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val colors = LocalAppColors.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = Typography.bodySmall.copy(
            color = colors.fontPrimary
        ),
        readOnly = readOnly,
        label = { Caption(label, colors.fontSecondary) },
        shape = RoundedCornerShape(8.dp),
        trailingIcon = {
            if (icon != null && value.isNotBlank())
                IconButton(onIconClick) {
                    Icon(
                        imageVector = ImageVector.vectorResource(icon),
                        contentDescription = iconDescription,
                        tint = colors.fontPrimary
                    )
                }
        },
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = if (value.isBlank()) colors.fontDisable else colors.fontPrimary,
            errorBorderColor = colors.indicatorError,
            cursorColor = colors.borderPrimary,
            focusedBorderColor = colors.borderPrimary,
            unfocusedContainerColor = if (value.isBlank()) colors.bgSecondary else colors.bgPrimary,
            errorSupportingTextColor = colors.indicatorError,
            unfocusedBorderColor = if (value.isBlank()) colors.bgSecondary else colors.fontPrimary
        ),
        supportingText = {
            if (isError)
                Caption(supportingText, colors.indicatorError)
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewBasicInputField() {
    LoansAppTheme {
        BasicInputField(
            "",
            {},
            "Login",
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            R.drawable.eye_cross,
            isError = false,
            supportingText = "smth wrong",
            visualTransformation = PasswordVisualTransformation()
        )
    }
}
