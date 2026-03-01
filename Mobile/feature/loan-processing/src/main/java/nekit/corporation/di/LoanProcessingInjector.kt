package nekit.corporation.di

import nekit.corporation.presentation.LoanProcessingFragment

interface LoanProcessingInjector {

    fun inject(fragment: LoanProcessingFragment)
}