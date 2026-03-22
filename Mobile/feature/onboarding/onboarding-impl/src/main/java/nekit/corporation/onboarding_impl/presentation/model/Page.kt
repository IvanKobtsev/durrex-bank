package nekit.corporation.onboarding_impl.presentation.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

internal data class Page(
    @param:DrawableRes val image: Int,
    @param:StringRes val label: Int,
    @param:StringRes val description: Int
)
