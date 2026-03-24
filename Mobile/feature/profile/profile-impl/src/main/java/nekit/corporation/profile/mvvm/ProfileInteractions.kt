package nekit.corporation.profile.mvvm

import nekit.corporation.user.domain.model.Scheme


internal interface ProfileInteractions {
    fun onSchemeSwitch(scheme: Scheme)

    fun onSchemeClick()
}