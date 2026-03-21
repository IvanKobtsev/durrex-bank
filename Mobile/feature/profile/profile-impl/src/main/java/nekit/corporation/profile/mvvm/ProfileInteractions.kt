package nekit.corporation.profile.mvvm

import android.content.Context
import kotlinx.coroutines.flow.SharedFlow
import nekit.corporation.user.domain.model.Language
import nekit.corporation.user.domain.model.Scheme


internal interface ProfileInteractions {
    val uiFlow: SharedFlow<UiEvents>
    fun onSchemeSwitch(scheme: Scheme)

    fun onSchemeClick()

    fun onLanguageChange(context: Context, language: Language)

    fun onLanguageClick()
}