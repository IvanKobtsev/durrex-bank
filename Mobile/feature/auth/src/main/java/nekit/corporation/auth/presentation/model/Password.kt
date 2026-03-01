package nekit.corporation.auth.presentation.model

import androidx.annotation.StringRes

data class Password(
    val password: String,
    @StringRes val error: Int?,
    val isVisible: Boolean,
    val isObserverActive: Boolean
)
