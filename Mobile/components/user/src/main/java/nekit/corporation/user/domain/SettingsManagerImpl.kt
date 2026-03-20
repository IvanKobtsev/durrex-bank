package nekit.corporation.user.domain

import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.optional.SingleIn
import dagger.BindsInstance
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import nekit.corporation.common.AppScope
import nekit.corporation.user.domain.model.Settings

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class SettingsManagerImpl @Inject constructor() : SettingsManager {

    override val settings: MutableStateFlow<Settings?> = MutableStateFlow(null)

    override suspend fun update(settings: (Settings?) -> Settings?) {
        this.settings.emit(settings(this.settings.value))
    }
}