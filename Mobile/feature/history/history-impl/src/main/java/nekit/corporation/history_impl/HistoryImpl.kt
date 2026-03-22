package nekit.corporation.history_impl

import androidx.lifecycle.ViewModelProvider
import com.github.terrakok.cicerone.androidx.FragmentScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import nekit.corporation.history_api.HistoryApi
import nekit.corporation.history_impl.presentation.menu.HistoryFragment
import nekit.corporation.history_impl.presentation.all.accounts.AllAccountsFragment
import nekit.corporation.history_impl.presentation.all.loans.AllLoansFragment

@Inject
@ContributesBinding(AppScope::class)
class HistoryImpl(
    val viewModelFactory: ViewModelProvider.Factory
) : HistoryApi {

    override fun history() = FragmentScreen {
        HistoryFragment(viewModelFactory)
    }

    override fun allAccounts() = FragmentScreen {
        AllAccountsFragment(viewModelFactory)
    }

    override fun allLoans() = FragmentScreen {
        AllLoansFragment(viewModelFactory)
    }
}