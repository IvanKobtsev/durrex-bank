package nekit.corporation.create_loan_impl.model

import nekit.corporation.loan_shared.domain.model.Account

internal fun Account.toUi() = AccountUi(
    id = id,
    balance = balance,
    currency = currency
)