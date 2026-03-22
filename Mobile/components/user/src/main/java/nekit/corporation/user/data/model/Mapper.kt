package nekit.corporation.user.data.model

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
        Scheme.Light
    },
    hiddenAccountIds = hiddenAccountIds
)

fun Settings.toSettingsDto() = SettingsDto(
    theme = theme.name,
    hiddenAccountIds = hiddenAccountIds
)

