package nekit.corporation.auth_impl.presentation.model

import androidx.annotation.StringRes

data class Field(
    val text: String,
    @param:StringRes val error: Int?,
    val isObserverActive: Boolean
)
