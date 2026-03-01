package nekit.corporation.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
import nekit.corporation.ui.component.Headline
import nekit.corporation.ui.component.HighlightedText
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.ui.theme.Typography

@Composable
fun OnboardingPage(
    @DrawableRes placeholderIdRes: Int,
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            painter = painterResource(placeholderIdRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            Headline(
                text = title,
                color = colors.fontPrimary,
                modifier = Modifier
                    .padding(vertical = 12.dp)
            )

            HighlightedText(
                fullText = body,
                wordsToHighlight = persistentListOf(stringResource(R.string.bank_office)),
                style = Typography.titleMedium,
                highlightColor = colors.permanentPrimaryDark,
                defaultColor = colors.fontPrimary
            )
        }
    }
}

