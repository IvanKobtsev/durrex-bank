package nekit.corporation.transaction_impl

import androidx.lifecycle.ViewModelProvider
import com.github.terrakok.cicerone.androidx.FragmentScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import nekit.corporation.transaction_api.TransactionApi

@Inject
@ContributesBinding(AppScope::class)
class TransactionImpl(
    private val viewModelFactory: ViewModelProvider.Factory
) : TransactionApi {


    override fun transaction() = FragmentScreen {
        TransactionFragment(viewModelFactory)
    }
}