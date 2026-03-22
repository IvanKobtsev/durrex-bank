package nekit.corporation.profile.model

import nekit.corporation.user.domain.model.Settings
import nekit.corporation.user.domain.model.User

internal fun Settings.toUi() = SettingsUi(
    scheme = theme
)

internal fun User.toAccountModel(rating: Int?) = AccountModel(
    firstName = firstName,
    lastName = lastName,
    email = email,
    phone = telephoneNumber,
    isBlocked = isBlocked,
    rating = rating
)