package nekit.corporation.presentation.model

interface AccountDetailsInteractions {

    fun onBack()

    fun onDebitClick()

    fun onDepositClick()

    fun onDepositSumChange(deposit: String)

    fun onDebiSumChange(debit: String)
}