package nekit.corporation.user.domain

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.MutableStateFlow
import nekit.corporation.user.domain.model.Scheme


@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, binding = binding<SettingsManager>())
class SettingsManagerImpl @Inject constructor() : SettingsManager {

    override val settings: MutableStateFlow<Scheme?> = MutableStateFlow(null)

    override suspend fun update(theme: Scheme) {
        this.settings.emit(theme)
    }
}