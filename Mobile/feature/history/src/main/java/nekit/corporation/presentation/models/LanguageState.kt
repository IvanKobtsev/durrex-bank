package nekit.corporation.presentation.models

import nekit.corporation.architecture.presentation.ScreenState

data class LanguageState(
    val selectedLanguage: Language
) : ScreenState