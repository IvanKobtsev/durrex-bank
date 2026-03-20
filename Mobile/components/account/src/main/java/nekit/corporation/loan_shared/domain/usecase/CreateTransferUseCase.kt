package nekit.corporation.loan_shared.domain.usecase

import nekit.corporation.loan_shared.domain.model.Transaction
import nekit.corporation.loan_shared.domain.model.Transfer
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import javax.inject.Inject

class CreateTransferUseCase @Inject constructor(
    val accountRepository: AccountRepository
) {

    suspend operator fun invoke(id: Int, transfer: Transfer): Transaction {
        return accountRepository.createTransfer(id, transfer)
    }
}