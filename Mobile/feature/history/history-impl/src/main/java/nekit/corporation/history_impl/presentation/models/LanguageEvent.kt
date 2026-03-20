package nekit.corporation.presentation.models

import nekit.corporation.architecture.presentation.Event

sealed interface LanguageEvent : Event {

    data object LanguageApplied : LanguageEvent
}
