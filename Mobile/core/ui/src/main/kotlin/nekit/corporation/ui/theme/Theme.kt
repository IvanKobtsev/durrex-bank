@file:Suppress("DEPRECATION")

package nekit.corporation.ui.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import java.util.Locale

val LocalAppColors = staticCompositionLocalOf { LightAppColors }

@SuppressLint("LocalContextConfigurationRead")
@Composable
fun LoansAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val configuration = remember {
        Configuration(context.resources.configuration).apply {
            setLocale(Locale(LocaleManager.getPersistedLanguage(context)))
        }
    }

    val localizedContext = remember(configuration) {
        context.createConfigurationContext(configuration)
    }


    val colors = if (darkTheme) DarkAppColors else LightAppColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val context = view.context
            if (context is Activity) {
                context.window?.let {
                    WindowCompat.getInsetsController(it, view).apply {
                        isAppearanceLightStatusBars = !darkTheme
                        isAppearanceLightNavigationBars = !darkTheme
                    }
                }
            }
        }
    }
    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalContext provides localizedContext
    ) {
        MaterialTheme(
            typography = Typography,
            content = content
        )
    }
}