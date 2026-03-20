package nekit.corporation.user.domain

import kotlinx.coroutines.flow.StateFlow
import nekit.corporation.user.domain.model.Settings

interface SettingsManager {

    val settings: StateFlow<Settings?>

    suspend fun update(settings: (Settings?) -> Settings?)
}