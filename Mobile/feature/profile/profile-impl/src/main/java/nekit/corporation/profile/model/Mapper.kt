package nekit.corporation.profile.model

import nekit.corporation.user.domain.model.Settings
import nekit.corporation.user.domain.model.User

internal fun Settings.toUi() = SettingsUi(
    language = language,
    scheme = scheme
)

internal fun SettingsUi.toDomain() = Settings(
    language = language,
    scheme = scheme
)

internal fun User.toAccountModel() = AccountModel(
    firstName = firstName,
    lastName = lastName,
    email = email,
    phone = telephoneNumber,
    isBlocked = isBlocked,
    rating = rating
)