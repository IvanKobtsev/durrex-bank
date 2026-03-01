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
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import nekit.corporation.ui.theme.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropText(
    selected: String,
    variants: ImmutableList<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (String) -> Unit,
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
            value = selected,
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
            variants.forEach { text ->
                DropdownMenuItem(
                    text = { Body2Text(text) },
                    onClick = { onSelect(text) },
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
            .padding(horizontal = 16.dp)
    )

}