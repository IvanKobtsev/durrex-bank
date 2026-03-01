package nekit.corporation.di

import nekit.corporation.presentation.state.LoanStateFragment

interface LoanStateInjector {

    fun inject(fragment: LoanStateFragment)
}