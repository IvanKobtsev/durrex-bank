package nekit.corporation.ui.theme

import androidx.compose.ui.graphics.Color

data class AppColors(
    val bgPrimary: Color,
    val bgSecondary: Color,
    val bgTertiary: Color,
    val bgDisable: Color,
    val bgInvert: Color,

    val fontPrimary: Color,
    val fontSecondary: Color,
    val fontDisable: Color,
    val fontInvert: Color,

    val permanentPrimary: Color,
    val permanentPrimaryLight: Color,
    val permanentPrimaryDark: Color,

    val indicatorError: Color,
    val indicatorPositive: Color,
    val indicatorAttention: Color,

    val borderPrimary: Color,
    val borderSecondary: Color,

    val iconPrimary: Color,
    val iconSecondary: Color,
    val iconDisable: Color,
    val iconInvert: Color,
)

val LightAppColors = AppColors(
    bgPrimary = bg_primary,
    bgSecondary = bg_secondary,
    bgTertiary = bg_tertiary,
    bgDisable = bg_disable,
    bgInvert = bg_invert,

    fontPrimary = font_primary,
    fontSecondary = font_secondary,
    fontDisable = font_disable,
    fontInvert = font_invert,

    permanentPrimary = permanent_primary,
    permanentPrimaryLight = permanent_primary_light,
    permanentPrimaryDark = permanent_primary_dark,

    indicatorError = indicator_error,
    indicatorPositive = indicator_positive,
    indicatorAttention = indicator_attention,

    borderPrimary = border_primary,
    borderSecondary = border_secondary,

    iconPrimary = icon_primary,
    iconSecondary = icon_secondary,
    iconDisable = icon_disable,
    iconInvert = icon_invert,
)

val DarkAppColors = AppColors(
    bgPrimary = dark_bg_primary,
    bgSecondary = dark_bg_secondary,
    bgTertiary = dark_bg_tertiary,
    bgDisable = dark_bg_disable,
    bgInvert = dark_bg_invert,

    fontPrimary = dark_font_primary,
    fontSecondary = dark_font_secondary,
    fontDisable = dark_font_disable,
    fontInvert = dark_font_invert,

    permanentPrimary = dark_permanent_primary,
    permanentPrimaryLight = dark_permanent_primary_light,
    permanentPrimaryDark = dark_permanent_primary_dark,

    indicatorError = dark_indicator_error,
    indicatorPositive = dark_indicator_positive,
    indicatorAttention = dark_indicator_attention,

    borderPrimary = dark_border_primary,
    borderSecondary = dark_border_secondary,

    iconPrimary = dark_icon_primary,
    iconSecondary = dark_icon_secondary,
    iconDisable = dark_icon_disable,
    iconInvert = dark_icon_invert,
)
