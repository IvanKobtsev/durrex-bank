package nekit.corporation.user.data.repository

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import nekit.corporation.user.data.model.UpdateHiddenAccountsDto
import nekit.corporation.user.data.model.UpdateThemeDto
import nekit.corporation.user.data.model.toSettings
import nekit.corporation.user.data.model.toSettingsDto
import nekit.corporation.user.data.model.toUser
import nekit.corporation.user.data.remote.SettingsApi
import nekit.corporation.user.data.remote.UserApi
import nekit.corporation.user.domain.UserRepository
import nekit.corporation.user.domain.model.Scheme
import nekit.corporation.user.domain.model.Settings
import nekit.corporation.user.domain.model.User

@Inject
@ContributesBinding(AppScope::class)
class UserRepositoryImpl(
    private val userApi: UserApi,
    private val settingsApi: SettingsApi
) : UserRepository {

    override suspend fun getUser(): User {
        return userApi.getUser().toUser()
    }

    override suspend fun getUser(id: Int): User {
        return userApi.getUserById(id).toUser()
    }

    override suspend fun getSettings(): Settings {
        return settingsApi.getSettings().toSettings()
    }

    override suspend fun saveSettings(settings: Settings): Settings {
        return settingsApi.setSettings(settings.toSettingsDto()).toSettings()
    }

    override suspend fun updateHidden(
        added: List<Int>,
        removed: List<Int>
    ): Settings {
        return settingsApi.updateHiddenAccounts(UpdateHiddenAccountsDto(added, removed))
            .toSettings()
    }

    override suspend fun updateTheme(theme: Scheme): Settings {
        return settingsApi.updateTheme(UpdateThemeDto(theme)).toSettings()

    }
}