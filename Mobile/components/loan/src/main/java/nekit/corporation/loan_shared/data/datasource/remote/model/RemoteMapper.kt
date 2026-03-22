package nekit.corporation.loan_shared.data.datasource.remote.model

import nekit.corporation.loan_shared.domain.model.Credit
import nekit.corporation.loan_shared.domain.model.CreditDetail
import nekit.corporation.loan_shared.domain.model.CreditStatusDomain
import nekit.corporation.loan_shared.domain.model.IssueCredit
import nekit.corporation.loan_shared.domain.model.Overdue
import nekit.corporation.loan_shared.domain.model.PaymentScheduleEntry
import nekit.corporation.loan_shared.domain.model.Rating

fun CreditResponse.toDomain() = Credit(
    id = id,
    clientId = clientId,
    accountId = accountId,
    tariffName = tariffName,
    amount = amount,
    remainingBalance = remainingBalance,
    status =
        CreditStatusDomain.entries[status],
    issuedAt = issuedAt
)

fun CreditDetailResponse.toDomain() = CreditDetail(
    id = id,
    clientId = clientId,
    accountId = accountId,
    tariffName = tariffName,
    amount = amount,
    remainingBalance = remainingBalance,
    status = CreditStatusDomain.entries[status],
    issuedAt = issuedAt,
    nextPaymentDate = nextPaymentDate,
    schedule = schedule?.map { it.toDomain() }
)

fun PaymentScheduleEntryResponse.toDomain() = PaymentScheduleEntry(
    id = id,
    dueDate = dueDate,
    amount = amount,
    isPaid = isPaid,
    paidAt = paidAt
)

fun IssueCreditRequest.toDomain() = IssueCredit(
    accountId = accountId,
    tariffId = tariffId,
    amount = amount
)

fun RatingDto.toDomain() = Rating(
    clientId = clientId,
    rating = rating,
    calculatedAt = calculatedAt
)

fun OverdueDto.toDomain() = Overdue(
    entryId = entryId,
    creditId = creditId,
    dueDate = dueDate,
    amount = amount,
    daysOverdue = daysOverdue
)