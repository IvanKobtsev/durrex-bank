package nekit.corporation.create_loan_impl.model

internal interface CreateLoanInteractions {

    fun onBackClick()

    fun onSelectTariff(tariff: String)

    fun onExpandedTariffChange(isOpen: Boolean)

    fun onExpandedAccountChange(isOpen: Boolean)

    fun onSelectAccount(account: AccountUi)

    fun onCreateCredit()

    fun onChangeAmount(amount: String)
}