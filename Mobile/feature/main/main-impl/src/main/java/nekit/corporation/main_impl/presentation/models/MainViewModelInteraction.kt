package nekit.corporation.main_impl.presentation.models

internal interface MainViewModelInteraction {

    fun openOnboarding()


    fun onCreateCreditClick()

    fun onAccountCreateClick()

    fun onShowAllLoansClick()

    fun onShowAllAccountsClick()

    fun onLoanClick(id: Int)

    fun onDismissCurrency()

    fun onSelectCurrency(currency: Currency)

    fun onOpenCurrencyMenu()

    fun onAccountClick(id: Int)

    fun onHiddenSwitch()

    fun onCreateTransactionClick()
}