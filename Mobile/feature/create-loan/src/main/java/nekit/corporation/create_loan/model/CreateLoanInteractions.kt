package nekit.corporation.create_loan.model

interface CreateLoanInteractions {

    fun onBackClick()

    fun onSelectTariff(tariff: String)

    fun onExpandedTariffChange(isOpen: Boolean)

    fun onSelectAccount(account: String)

    fun onCreateCredit()

    fun onChangeAmount(amount: String)
}