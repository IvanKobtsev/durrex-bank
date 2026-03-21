package nekit.corporation.history_impl.presentation.menu.mvvm

import androidx.annotation.StringRes
import nekit.corporation.architecture.presentation.Event

internal sealed interface HistoryEvent : Event {

    class ShowToast(@field:StringRes val res: Int) : HistoryEvent
}