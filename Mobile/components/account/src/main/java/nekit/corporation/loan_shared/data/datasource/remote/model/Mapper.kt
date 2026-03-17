package nekit.corporation.loan_shared.data.datasource.remote.model

import nekit.corporation.loan_shared.domain.model.Account
import nekit.corporation.loan_shared.domain.model.CreateAccount
import nekit.corporation.loan_shared.domain.model.Debit
import nekit.corporation.loan_shared.domain.model.Deposit
import nekit.corporation.loan_shared.domain.model.PagedTransactions
import nekit.corporation.loan_shared.domain.model.Transaction
import nekit.corporation.loan_shared.domain.model.TransactionTypeDomain
import nekit.corporation.loan_shared.domain.model.Withdraw


fun Deposit.toDepositRequest() =
    DepositRequest(
        amount = amount,
        description = description
    )

fun Debit.toDebitRequest() =
    DebitRequest(
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
    status = AccountStatus.entries[status],
    createdAt = createdAt,
    closedAt = closedAt
)

fun CreateAccount.toCreateAccountCommand() =
    CreateAccountCommand(
        currency = currency
    )


fun PagedResponseOfTransactionResponse.toDomain() = PagedTransactions(
    items = items.map { it.toDomain() },
    page = page,
    pageSize = pageSize,
    totalCount = totalCount,
    totalPages = totalPages
)

fun TransactionResponse.toDomain() = Transaction(
    id = id,
    accountId = accountId,
    type = TransactionTypeDomain.entries[type],
    amount = amount,
    balanceBefore = balanceBefore,
    balanceAfter = balanceAfter,
    relatedAccountId = relatedAccountId,
    description = description,
    createdAt = createdAt.toInstant()
)