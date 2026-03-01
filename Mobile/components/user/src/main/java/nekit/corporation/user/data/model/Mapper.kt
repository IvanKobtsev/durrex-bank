package nekit.corporation.user.data.model

import nekit.corporation.user.domain.model.PagedTransactions
import nekit.corporation.user.domain.model.Transaction
import nekit.corporation.user.domain.model.TransactionTypeDomain
import nekit.corporation.user.domain.model.User

fun UserResponse.toUser() = User(
    id = id,
    username = username,
    firstName = firstName,
    lastName = lastName,
    email = email,
    telephoneNumber = telephoneNumber,
    role = role,
    isBlocked = isBlocked
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
    type = when (type) {
        TransactionType.DEPOSIT -> TransactionTypeDomain.DEPOSIT
        TransactionType.WITHDRAW -> TransactionTypeDomain.WITHDRAW
        TransactionType.TRANSFER -> TransactionTypeDomain.TRANSFER
        TransactionType.DEBIT -> TransactionTypeDomain.DEBIT
    },
    amount = amount,
    balanceBefore = balanceBefore,
    balanceAfter = balanceAfter,
    relatedAccountId = relatedAccountId,
    description = description,
    createdAt = createdAt
)