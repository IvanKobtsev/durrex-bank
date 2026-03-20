package nekit.corporation.transaction_details.di

import nekit.corporation.transaction_details.presentation.TransactionDetailsFragment


interface TransactionDetailsFragmentInjector {

    fun inject(fragment: TransactionDetailsFragment)
}