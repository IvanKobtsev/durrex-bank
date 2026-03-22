package nekit.corporation.profile.mvvm

import nekit.corporation.architecture.presentation.ScreenState
import nekit.corporation.profile.model.AccountModel
import nekit.corporation.profile.model.SettingsUi

data class ProfileState(
    val isLoading: Boolean,
    val account: AccountModel?,
    val settings: SettingsUi?,
    val isSchemeOpen: Boolean,
    val isLanguageOpen: Boolean
) : ScreenState{

    companion object{
        val DEFAULT = ProfileState(
            isLoading = true,
            account = null,
            settings = null,
            isSchemeOpen = false,
            isLanguageOpen = false
        )
    }
}
