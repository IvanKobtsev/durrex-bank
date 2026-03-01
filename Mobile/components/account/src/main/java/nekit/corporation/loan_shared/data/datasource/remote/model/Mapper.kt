package nekit.corporation.loan_shared.data.datasource.remote.model

import nekit.corporation.loan_shared.domain.model.Account
import nekit.corporation.loan_shared.domain.model.CreateAccount
import nekit.corporation.loan_shared.domain.model.Deposit
import nekit.corporation.loan_shared.domain.model.Withdraw


fun Deposit.toDepositRequest() =
    DepositRequest(
        amount = amount,
        description = description
    )

fun Withdraw.toWithdrawRequest() =
    WithdrawRequest(
        amount = amount,
        description = description
    )

fun AccountResponse.toAccount() = Account(
    id = id,
    ownerId = ownerId,
    balance = balance,
    currency = currency,
    status = status,
    createdAt = createdAt,
    closedAt = closedAt
)

fun CreateAccount.toCreateAccountCommand() =
    CreateAccountCommand(
        ownerId = ownerId,
        currency = currency
    )