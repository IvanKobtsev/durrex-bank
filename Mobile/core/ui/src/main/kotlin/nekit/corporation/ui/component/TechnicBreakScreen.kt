package nekit.corporation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nekit.corporation.ui.R
import nekit.corporation.ui.theme.DurexBankTheme
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.ui.theme.sairaStencilOneFontFamily

@Composable
fun TechnicBreakScreen(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    Card() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .background(Color(if (colors.isDark) 0xFF040307 else 0xFFf8f7f9))
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Image(
                painter = painterResource(
                    id = if (colors.isDark) R.drawable.img_technic_break_dark
                    else R.drawable.img_technic_break_light
                ),
                contentDescription = null,
                modifier = Modifier.size(220.dp),
                contentScale = ContentScale.Fit
            )

            Headline(
                stringResource(R.string.technic_break),
                modifier = Modifier
                    .padding(32.dp),
                textAlign = TextAlign.Center,
                color = colors.fontDisable,
                font = sairaStencilOneFontFamily
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF040307)
@Composable
private fun PreviewScreen() {
    DurexBankTheme(false) {
        TechnicBreakScreen()
    }
}