package nekit.corporation.account_details_impl

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.github.terrakok.cicerone.androidx.FragmentScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import nekit.corporation.account_details_api.AccountDetailsApi
import nekit.corporation.account_details_impl.presentation.AccountDetailsFragment

@Inject
@ContributesBinding(AppScope::class)
class AccountDetailsImpl(val viewModelFactory: ViewModelProvider.Factory) : AccountDetailsApi {

    override fun accountDetails(id: Int) = FragmentScreen {
        AccountDetailsFragment(viewModelFactory).apply {
            arguments = Bundle().apply {
                putInt(AccountDetailsFragment.ID_ARG, id)
            }
        }
    }
}