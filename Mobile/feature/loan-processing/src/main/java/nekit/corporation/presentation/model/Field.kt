package nekit.corporation.presentation.model

import androidx.annotation.StringRes

data class Field(
    val text: String,
    @StringRes val error: Int?,
    val isObserved: Boolean,
)