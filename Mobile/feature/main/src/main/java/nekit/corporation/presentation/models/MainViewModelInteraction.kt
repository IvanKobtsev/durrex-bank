package nekit.corporation.presentation.models

import nekit.corporation.domain.Currency

interface MainViewModelInteraction {

    fun openOnboarding()


    fun onCreateCreditClick()

    fun onAccountCreateClick()

    fun onShowAllLoansClick()

    fun onLoanClick(id: Int)

    fun onDismissCurrency()

    fun onSelectCurrency(currency: Currency)

    fun onOpenCurrencyMenu()
}