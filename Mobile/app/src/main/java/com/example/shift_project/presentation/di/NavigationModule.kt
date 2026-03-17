package com.example.shift_project.presentation.di

import com.example.shift_project.presentation.navigation.AccountDetailsNavigator
import com.example.shift_project.presentation.navigation.AllAccountsNavigator
import com.example.shift_project.presentation.navigation.AllLoansNavigator
import com.example.shift_project.presentation.navigation.AuthNavigator
import com.example.shift_project.presentation.navigation.CreateCreditNavigator
import com.example.shift_project.presentation.navigation.LoanDetailsNavigator
import com.example.shift_project.presentation.navigation.MainNavigator
import com.example.shift_project.presentation.navigation.MenuNavigator
import com.example.shift_project.presentation.navigation.OnboardingNavigator
import com.example.shift_project.presentation.navigation.TransactionDetailsNavigator
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import nekit.corporation.auth.navigation.AuthNavigation
import nekit.corporation.common.AppScope
import nekit.corporation.create_loan.navigation.CreateCreditNavigation
import nekit.corporation.navigation.AccountDetailsNavigation
import nekit.corporation.navigation.AllAccountsNavigation
import nekit.corporation.navigation.AllLoansNavigation
import nekit.corporation.navigation.LoanDetailsNavigation
import nekit.corporation.navigation.MainNavigation
import nekit.corporation.navigation.MenuNavigation
import nekit.corporation.onboarding.navigation.OnboardingNavigation
import nekit.corporation.transaction_details.navigation.TransactionDetailsNavigation

@ContributesTo(AppScope::class)
@Module
interface NavigationModule {

    @Binds
    fun bindAuthNavigator(impl: AuthNavigator): AuthNavigation

    @Binds
    fun bindOnboardingNavigator(impl: OnboardingNavigator): OnboardingNavigation

    @Binds
    fun bindMainNavigator(impl: MainNavigator): MainNavigation

    @Binds
    fun bindMenuNavigator(impl: MenuNavigator): MenuNavigation

    @Binds
    fun bindLoanDetailsNavigator(impl: LoanDetailsNavigator): LoanDetailsNavigation

    @Binds
    fun bindAccountDetailsNavigator(impl: AccountDetailsNavigator): AccountDetailsNavigation

    @Binds
    fun bindAllAccountsNavigator(impl: AllAccountsNavigator): AllAccountsNavigation

    @Binds
    fun bindAllLoansNavigator(impl: AllLoansNavigator): AllLoansNavigation

    @Binds
    fun bindCreateNavigator(impl: CreateCreditNavigator): CreateCreditNavigation

    @Binds
    fun bindTransactionDetailsNavigator(impl: TransactionDetailsNavigator): TransactionDetailsNavigation
}
