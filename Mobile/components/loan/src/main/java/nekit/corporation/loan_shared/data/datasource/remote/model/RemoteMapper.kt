package nekit.corporation.loan_shared.data.datasource.remote.model

import nekit.corporation.loan_shared.domain.model.Credit
import nekit.corporation.loan_shared.domain.model.CreditDetail
import nekit.corporation.loan_shared.domain.model.CreditStatusDomain
import nekit.corporation.loan_shared.domain.model.IssueCredit
import nekit.corporation.loan_shared.domain.model.PaymentScheduleEntry

fun CreditResponse.toDomain() = Credit(
    id = id,
    clientId = clientId,
    accountId = accountId,
    tariffName = tariffName,
    amount = amount,
    remainingBalance = remainingBalance,
    status = when (status) {
        CreditStatus.ACTIVE -> CreditStatusDomain.ACTIVE
        CreditStatus.CLOSED -> CreditStatusDomain.CLOSED
    },
    issuedAt = issuedAt
)

fun CreditDetailResponse.toDomain() = CreditDetail(
    id = id,
    clientId = clientId,
    accountId = accountId,
    tariffName = tariffName,
    amount = amount,
    remainingBalance = remainingBalance,
    status = when (status) {
        CreditStatus.ACTIVE -> CreditStatusDomain.ACTIVE
        CreditStatus.CLOSED -> CreditStatusDomain.CLOSED
    },
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
