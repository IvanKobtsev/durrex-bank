package nekit.corporation.user.domain

import kotlinx.coroutines.flow.StateFlow
import nekit.corporation.user.domain.model.Scheme
import nekit.corporation.user.domain.model.Settings

interface SettingsManager {

    val settings: StateFlow<Scheme?>

    suspend fun update(theme: Scheme)
}