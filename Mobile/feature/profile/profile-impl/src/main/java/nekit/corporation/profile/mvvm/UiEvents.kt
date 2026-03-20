package nekit.corporation.profile.mvvm

import androidx.annotation.StringRes

internal sealed interface UiEvents {

    class ShowToast(@param:StringRes val textRes: Int):UiEvents
}