package nekit.corporation.auth_impl.presentation.model

import androidx.annotation.StringRes

data class Password(
    val password: String,
    @param:StringRes val error: Int?,
    val isVisible: Boolean,
    val isObserverActive: Boolean
)
