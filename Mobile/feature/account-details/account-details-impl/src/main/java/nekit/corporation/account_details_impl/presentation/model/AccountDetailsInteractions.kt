package nekit.corporation.account_details_impl.presentation.model

import nekit.corporation.loan_shared.domain.model.Transaction
import nekit.corporation.loan_shared.domain.model.TransactionTypeDomain

interface AccountDetailsInteractions {

    fun onBack()

    fun onApplyClick()

    fun onSumChange(sum: String)

    fun onDeleteClick()

    fun onHide()

    fun onSelectOperation(operation: TransactionTypeDomain)

    fun onDismiss()

    fun onOpen()

    fun onTransactionOpen(transaction: Transaction)
}