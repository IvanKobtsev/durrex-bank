package nekit.corporation.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.ui.theme.Typography
import nekit.corporation.ui.theme.firaSansFontFamily


@Composable
fun Caption(string: String, color: Color, modifier: Modifier = Modifier) {
    Text(string, style = Typography.labelSmall, color = color, modifier = modifier)
}

@Composable
fun Body2Text(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    color: Color = Typography.bodySmall.color
) {
    Text(
        text = text,
        style = Typography.bodySmall,
        color = color,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun BodyText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    color: Color = Typography.bodyMedium.color
) {
    Text(
        text = text,
        style = Typography.bodyMedium,
        color = color,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun LogoText(text: String, color: Color) {
    Text(
        text = text.uppercase(),
        fontFamily = firaSansFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 43.sp,
        color = color,
    )
}

@Composable
fun Headline(
    text: String,
    color: Color = LocalAppColors.current.fontPrimary,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = Typography.headlineMedium,
        color = color,
        modifier = modifier,
    )
}

@Composable
fun Headline2(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = Typography.headlineSmall,
        color = color,
        modifier = modifier,
    )
}

@Composable
fun Body3Text(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    color: Color = Typography.bodyMedium.color
) {
    Text(
        text = text,
        style = Typography.titleMedium,
        color = color,
        modifier = modifier,
        textAlign = textAlign
    )
}


@Composable
fun HighlightedText(
    fullText: String,
    wordsToHighlight: ImmutableList<String>,
    highlightColor: Color,
    style: TextStyle,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    defaultColor: Color = Typography.bodyMedium.color
) {
    val annotatedString = buildAnnotatedString {
        if (wordsToHighlight.isEmpty()) {
            append(fullText)
        } else {
            val pattern = wordsToHighlight.joinToString("|") { Regex.escape(it) }
                .toRegex(RegexOption.IGNORE_CASE)

            var lastIndex = 0
            pattern.findAll(fullText).forEach { matchResult ->
                val beforeMatch = fullText.substring(lastIndex, matchResult.range.first)
                if (beforeMatch.isNotEmpty()) {
                    append(beforeMatch)
                }

                withStyle(style = SpanStyle(color = highlightColor)) {
                    append(matchResult.value)
                }

                lastIndex = matchResult.range.last + 1
            }

            if (lastIndex < fullText.length) {
                append(fullText.substring(lastIndex))
            }
        }
    }

    Text(
        text = annotatedString,
        style = style,
        modifier = modifier,
        textAlign = textAlign,
        color = defaultColor
    )
}

