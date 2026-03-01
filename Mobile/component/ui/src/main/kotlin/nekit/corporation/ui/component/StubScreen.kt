package nekit.corporation.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nekit.corporation.ui.R
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun StubScreen(
    label: String,
    description: String?,
    buttonText: String,
    onButtonClick: () -> Unit,
    onCloseClick: () -> Unit,
    @DrawableRes imageRes: Int,
    isFullScreenDrawable: Boolean,
    modifier: Modifier = Modifier
) {

    val colors = LocalAppColors.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bgPrimary)
            .padding(
                top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
                bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
            )

    ) {
        BaseIconButton(onCloseClick)
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.run {
                    if (isFullScreenDrawable)
                        this.fillMaxWidth()
                    else
                        this.size(96.dp)
                },
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(28.dp))
            Headline2(
                text = label,
                color = colors.fontPrimary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(16.dp))
            description?.let {
                BodyText(
                    text = it,
                    color = colors.fontPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }


        BasicButton(
            text = buttonText,
            onClick = onButtonClick,
            isEnable = true,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview
@Composable
private fun PreviewStubScreen() {
    LoansAppTheme {
        StubScreen(
            label = "Специальное предложение",
            description = "Погасите займ досрочно и мы увеличим лимит для следующего займа",
            buttonText = "Ближайшее отделение банка",
            onButtonClick = { },
            onCloseClick = { },
            imageRes = R.drawable.question_ic,
            isFullScreenDrawable = false
        )
    }
}