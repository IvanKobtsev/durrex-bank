package nekit.corporation.ui.component

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import nekit.corporation.ui.theme.Typography

@Composable
fun EditableHeadline2(
    value: String,
    onValueChange: (String) -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Number
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val maxValueTextWidth =  with(density) {
        textMeasurer.measure(
            value,
            style = Typography.headlineSmall.copy(color = color)
        ).size.width.toDp()
    }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        textStyle = Typography.headlineSmall.copy(color = color),
        cursorBrush = SolidColor(color),
        modifier = modifier.width(maxValueTextWidth),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        )
    )
}