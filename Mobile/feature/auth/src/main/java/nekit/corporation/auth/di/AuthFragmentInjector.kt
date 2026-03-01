package nekit.corporation.auth.di

import nekit.corporation.auth.presentation.auth.AuthFragment

interface AuthFragmentInjector {

    fun inject(fragment: AuthFragment)
}
