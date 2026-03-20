package nekit.corporation.presentation.models

import androidx.annotation.StringRes

sealed class MenuItem(@StringRes open val name: Int) {

    data class MyLoans(override val name: Int) : MenuItem(name)

    data class SuggestionsForYou(override val name: Int) : MenuItem(name)

    data class BankOffices(override val name: Int) : MenuItem(name)

    data class Help(override val name: Int) : MenuItem(name)

    data class Language(override val name: Int) : MenuItem(name)

    data class Exit(override val name: Int) : MenuItem(name)
}
