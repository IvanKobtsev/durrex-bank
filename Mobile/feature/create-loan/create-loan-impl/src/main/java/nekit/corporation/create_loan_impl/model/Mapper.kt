package nekit.corporation.create_loan.model

import nekit.corporation.loan_shared.domain.model.Account

fun Account.toUi() = AccountUi(
    id = id,
    balance = balance,
    currency = currency
)