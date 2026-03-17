package com.example.shift_project.presentation.di

import android.content.Context
import com.example.shift_project.presentation.MainActivity
import com.github.terrakok.cicerone.Router
import com.squareup.anvil.annotations.MergeComponent
import com.squareup.anvil.annotations.optional.SingleIn
import dagger.BindsInstance
import nekit.corporation.auth.presentation.auth.AuthFragment
import nekit.corporation.common.AppScope
import nekit.corporation.create_loan.CreateCreditFragment
import nekit.corporation.onboarding.presentation.OnboardingFragment
import nekit.corporation.presentation.AccountDetailsFragment
import nekit.corporation.presentation.LoanDetailsFragment
import nekit.corporation.presentation.MainFragment
import nekit.corporation.presentation.ShellMainHostFragment
import nekit.corporation.presentation.all.accounts.AllAccountsFragment
import nekit.corporation.presentation.all.loans.AllLoansFragment
import nekit.corporation.presentation.menu.HistoryFragment
import nekit.corporation.transaction_details.presentation.TransactionDetailsFragment

@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
interface AppDaggerComponent {

    fun inject(activity: MainActivity)

    fun inject(fragment: AuthFragment)

    fun inject(fragment: OnboardingFragment)

    fun inject(fragment: HistoryFragment)

    fun inject(fragment: MainFragment)

    fun inject(fragment: ShellMainHostFragment)

    fun inject(fragment: LoanDetailsFragment)

    fun inject(fragment: AllLoansFragment)

    fun inject(fragment: CreateCreditFragment)

    fun inject(fragment: AllAccountsFragment)

    fun inject(fragment: AccountDetailsFragment)

    fun inject(fragment: TransactionDetailsFragment)

    @MergeComponent.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance router: Router,
        ): AppDaggerComponent
    }
}
