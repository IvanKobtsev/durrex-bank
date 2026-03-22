package nekit.corporation.user.data.model

import nekit.corporation.user.domain.model.Language
import nekit.corporation.user.domain.model.Scheme

data class SettingsDto(
    val scheme: Scheme,
    val language: Language
)
