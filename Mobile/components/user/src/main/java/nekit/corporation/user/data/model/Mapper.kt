package nekit.corporation.user.data.model

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
    rating = rating
)

