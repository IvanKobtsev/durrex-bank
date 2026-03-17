package com.example.shift_project.presentation.navigation

import android.os.Bundle
import android.provider.Settings.Global.putInt
import com.github.terrakok.cicerone.androidx.FragmentScreen
import nekit.corporation.auth.presentation.auth.AuthFragment
import nekit.corporation.create_loan.CreateCreditFragment
import nekit.corporation.onboarding.presentation.OnboardingFragment
import nekit.corporation.presentation.AccountDetailsFragment
import nekit.corporation.presentation.LoanDetailsFragment
import nekit.corporation.presentation.ShellMainHostFragment
import nekit.corporation.presentation.all.accounts.AllAccountsFragment
import nekit.corporation.presentation.all.loans.AllLoansFragment
import nekit.corporation.transaction_details.presentation.TransactionDetailsFragment
import nekit.corporation.transaction_details.ui.TransactionDetailsScreen

object Screens {

    fun auth() = FragmentScreen { AuthFragment() }

    fun onboarding() = FragmentScreen { OnboardingFragment() }

    fun mainShell() = FragmentScreen { ShellMainHostFragment() }

    fun creditDetails(id: Int) = FragmentScreen {
        LoanDetailsFragment().apply {
            arguments = Bundle().apply {
                putInt(LoanDetailsFragment.ID_ARG, id)
            }
        }
    }

    fun accountDetails(id: Int) = FragmentScreen {
        AccountDetailsFragment().apply {
            arguments = Bundle().apply {
                putInt(AccountDetailsFragment.ID_ARG, id)
            }
        }
    }

    fun createCredit() = FragmentScreen {
        CreateCreditFragment()
    }


    fun allLoans() =
        FragmentScreen {
            AllLoansFragment()
        }

    fun allAccounts() =
        FragmentScreen {
            AllAccountsFragment()
        }

    fun transactionDetails(accountId: Int, transactionId: Long) =
        FragmentScreen {
            TransactionDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(TransactionDetailsFragment.ID_ARG_ACCOUNT, accountId)
                    putLong(TransactionDetailsFragment.ID_ARG_TRANSACTION, transactionId)
                }
            }
        }
}