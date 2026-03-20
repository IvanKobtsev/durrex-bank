@file:OptIn(ExperimentalMaterial3Api::class)

package nekit.corporation.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.user.domain.model.Language
import nekit.corporation.user.domain.model.getRes
import okhttp3.internal.toImmutableList

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
        PrimaryInputField(
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

@Composable
fun <T> SelectionPanel(
    label: String,
    isVisible: Boolean,
    onExpandedChange: () -> Unit,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemLabel: @Composable (T) -> String,
    modifier: Modifier = Modifier
) {

    val colors = LocalAppColors.current
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.bgPrimary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 16.dp)
                .clickable(onClick = onExpandedChange)
        ) {
            BodyText(
                label
            )
            Spacer(
                Modifier
                    .weight(1f)
            )
            selectedItem?.let {
                BodyText(itemLabel(it))
            }
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically { it / 4 },
            exit = fadeOut() + slideOutVertically { -it / 4 }
        ) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                items.forEach { item ->
                    val isSelected = item == selectedItem
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onItemSelected(item) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BodyText(
                            text = itemLabel(item),
                            modifier = Modifier.weight(1f)
                        )
                        RadioButton(
                            selected = isSelected,
                            onClick = { onItemSelected(item) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = colors.permanentPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewDropText() {
    Box(Modifier.fillMaxSize()) {

    }
    SelectionPanel(
        label = "Выбранный язык:",
        isVisible = true,
        onExpandedChange = {},
        items = Language.entries.toImmutableList(),
        selectedItem = Language.En,
        onItemSelected = {},
        itemLabel = { el -> stringResource(el.getRes()) },
    )

}