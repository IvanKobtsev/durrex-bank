package nekit.corporation

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Inject
@ViewModelKey(ThemeViewModel::class)
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
class ThemeViewModel : ViewModel() {
    private val _darkTheme = MutableStateFlow(false)
    val darkTheme = _darkTheme.asStateFlow()

    fun toggleTheme() {
        _darkTheme.value = !_darkTheme.value
    }

    fun setTheme(dark: Boolean) {
        _darkTheme.value = dark
    }
}