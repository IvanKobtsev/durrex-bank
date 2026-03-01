package nekit.corporation.onboarding.presentation.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Page(
    @DrawableRes val image: Int,
    @StringRes val label: Int,
    @StringRes val description: Int
)
