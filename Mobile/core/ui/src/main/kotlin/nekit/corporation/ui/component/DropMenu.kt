package nekit.corporation.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.UiMode
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import nekit.corporation.ui.theme.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropText(
    selected: T?,
    variants: ImmutableList<T>,
    getDisplayText: (T) -> String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (T) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        BasicInputField(
            value = selected?.let(getDisplayText).orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = label,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            shape = RoundedCornerShape(12.dp),
            containerColor = colors.bgPrimary,
            border = BorderStroke(2.dp, colors.borderPrimary)
        ) {
            variants.forEach { item ->
                DropdownMenuItem(
                    text = { Body2Text(getDisplayText(item), color = colors.fontPrimary) },
                    onClick = { onSelect(item) },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewDropText() {
    Box(Modifier.fillMaxSize()) {

    }
    DropText(
        selected = "",
        variants = persistentListOf("asd", "asd"),
        expanded = true,
        onExpandedChange = {},
        onSelect = {},
        label = "asfasf",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        getDisplayText = { "asd" }
    )

}