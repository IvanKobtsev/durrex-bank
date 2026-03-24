package nekit.corporation.user.data.model

import android.util.Log
import nekit.corporation.user.domain.model.Scheme
import nekit.corporation.user.domain.model.Settings
import nekit.corporation.user.domain.model.User

fun UserResponse.toUser() = User(
    id = id,
    username = username,
    firstName = firstName,
    lastName = lastName,
    email = email,
    telephoneNumber = telephoneNumber,
    role = role,
    isBlocked = isBlocked,
)

fun SettingsDto.toSettings() = Settings(
    theme = try {
        Scheme.valueOf(theme)
    } catch (_: Throwable) {
        Log.d("RAG","fail get scheme")
        Scheme.light
    },
    hiddenAccountIds = hiddenAccountIds
)

fun Settings.toSettingsDto() = SettingsDto(
    theme = theme.name.lowercase(),
    hiddenAccountIds = hiddenAccountIds
)

