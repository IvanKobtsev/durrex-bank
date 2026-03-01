package nekit.corporation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import nekit.corporation.ui.R
import nekit.corporation.ui.theme.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseSlider(value: Float, onValueChange: (Float) -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    Slider(
        value = value,
        onValueChange = onValueChange,
        thumb = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.arrow_ic),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .background(colors.bgInvert, CircleShape),
                tint = colors.bgPrimary
            )
        },
        track = { sliderState ->

            val fraction by remember {
                derivedStateOf {
                    (sliderState.value - sliderState.valueRange.start) / (sliderState.valueRange.endInclusive - sliderState.valueRange.start)
                }
            }

            Box(Modifier.fillMaxWidth()) {
                Box(
                    Modifier
                        .fillMaxWidth(fraction)
                        .align(Alignment.CenterStart)
                        .height(4.dp)
                        .background(colors.bgInvert, CircleShape)
                )
                Box(
                    Modifier
                        .fillMaxWidth(1f - fraction)
                        .align(Alignment.CenterEnd)
                        .height(4.dp)
                        .background(colors.bgSecondary, CircleShape)
                )
            }
        },
        modifier = modifier
    )
}
