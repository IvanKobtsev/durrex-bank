package nekit.corporation.history_impl.presentation.models

import nekit.corporation.architecture.presentation.Event

internal sealed interface LanguageEvent : Event {

    data object LanguageApplied : LanguageEvent
}
