package com.example.shift_project.presentation

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.shift_project.R
import com.example.shift_project.databinding.LaunchScreenBinding
import com.example.shift_project.presentation.App.Companion.INSTANCE
import com.example.shift_project.presentation.di.AppDaggerComponent
import com.example.shift_project.presentation.navigation.Screens.auth
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.google.i18n.phonenumbers.PhoneNumberUtil
import nekit.corporation.auth.di.AuthFragmentInjector
import nekit.corporation.auth.presentation.auth.AuthFragment
import nekit.corporation.create_loan.CreateCreditFragment
import nekit.corporation.create_loan.navigation.CreateCreditInjector
import nekit.corporation.di.AccountDetailsFragmentInjector
import nekit.corporation.di.AllAccountsFragmentInjector
import nekit.corporation.di.AllLoansFragmentInjector
import nekit.corporation.di.HistoryFragmentInjector
import nekit.corporation.di.LoanDetailsFragmentInjector
import nekit.corporation.di.MainBottomBarInjector
import nekit.corporation.di.MainFragmentInjector
import nekit.corporation.onboarding.presentation.di.OnboardingFragmentInjector
import nekit.corporation.onboarding.presentation.OnboardingFragment
import nekit.corporation.presentation.AccountDetailsFragment
import nekit.corporation.presentation.LoanDetailsFragment
import nekit.corporation.presentation.MainFragment
import nekit.corporation.presentation.ShellMainHostFragment
import nekit.corporation.presentation.all.accounts.AllAccountsFragment
import nekit.corporation.presentation.all.loans.AllLoansFragment
import nekit.corporation.presentation.menu.HistoryFragment
import nekit.corporation.transaction_details.di.TransactionDetailsFragmentInjector
import nekit.corporation.transaction_details.navigation.TransactionDetailsNavigation
import nekit.corporation.transaction_details.presentation.TransactionDetailsFragment
import java.util.Locale
import kotlin.getValue

class MainActivity : AppCompatActivity(), AuthFragmentInjector, OnboardingFragmentInjector,
    HistoryFragmentInjector, MainFragmentInjector, MainBottomBarInjector,
    LoanDetailsFragmentInjector, AllLoansFragmentInjector, TransactionDetailsFragmentInjector,
    AllAccountsFragmentInjector, CreateCreditInjector, AccountDetailsFragmentInjector {

    override fun attachBaseContext(newBase: Context) {
        PhoneNumberUtil.getInstance(newBase)
        super.attachBaseContext(LocaleManager.getLocalizedContext(newBase))
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        super.applyOverrideConfiguration(
            overrideConfiguration?.let {
                val config = Configuration(it)
                config.setLocale(Locale(LocaleManager.getPersistedLanguage(this)))
                config
            }
        )
    }

    lateinit var component: AppDaggerComponent

    private val ciceroneNavigator by lazy {
        AppNavigator(this, R.id.main_container, supportFragmentManager)
    }
    private lateinit var binding: LaunchScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        INSTANCE.component.inject(this)
        component = INSTANCE.component

        binding = LaunchScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        INSTANCE.navigatorHolder.setNavigator(ciceroneNavigator)
        if (savedInstanceState == null) {
            INSTANCE.router.newRootScreen(auth())
        }
    }

    override fun onResume() {
        super.onResume()
        INSTANCE.navigatorHolder.setNavigator(ciceroneNavigator)
    }

    override fun onPause() {
        super.onPause()
        INSTANCE.navigatorHolder.removeNavigator()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun inject(fragment: AuthFragment) {
        component.inject(fragment)
    }

    override fun inject(fragment: OnboardingFragment) {
        component.inject(fragment)
    }

    override fun inject(fragment: MainFragment) {
        component.inject(fragment)
    }

    override fun inject(fragment: ShellMainHostFragment) {
        component.inject(fragment)
    }

    override fun inject(fragment: LoanDetailsFragment) {
        component.inject(fragment)
    }

    override fun inject(fragment: CreateCreditFragment) {
        component.inject(fragment)
    }

    override fun inject(fragment: HistoryFragment) {
        component.inject(fragment)
    }

    override fun inject(fragment: AllLoansFragment) {
        component.inject(fragment)
    }

    override fun inject(fragment: AllAccountsFragment) {
        component.inject(fragment)
    }

    override fun inject(fragment: AccountDetailsFragment) {
        component.inject(fragment)
    }

    override fun inject(fragment: TransactionDetailsFragment) {
        component.inject(fragment)
    }
}
