package nekit.corporation.transaction.model

import nekit.corporation.loan_shared.domain.model.Account
import nekit.corporation.user.domain.model.User

fun Account.toUi() = AccountUi(
    id = id,
    sum = balance,
    currency = currency
)

fun User.toUi() = UserUi(
    id = id,
    firstName = firstName,
    lastName = lastName
)