package nekit.corporation.profile.model

import nekit.corporation.user.domain.model.Language
import nekit.corporation.user.domain.model.Scheme

internal data class SettingsUi(
    val language: Language,
    val scheme: Scheme
)
