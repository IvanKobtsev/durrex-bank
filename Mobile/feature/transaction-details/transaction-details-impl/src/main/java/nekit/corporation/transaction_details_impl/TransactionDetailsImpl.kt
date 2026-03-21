package nekit.corporation.transaction_details_impl

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.github.terrakok.cicerone.androidx.FragmentScreen
import dev.zacsweers.metro.Inject
import nekit.corporation.transaction_details_impl.presentation.TransactionDetailsFragment
import nekit.corporation.transaction_details_api.TransactionDetailsApi

@Inject
class TransactionDetailsImpl(
    private val viewModelFactory: ViewModelProvider.Factory
) : TransactionDetailsApi {

    override fun transactionDetails(
        accountId: Int,
        transactionId: Long
    ) = FragmentScreen {
        TransactionDetailsFragment(viewModelFactory).apply {
            arguments = Bundle().apply {
                putInt(TransactionDetailsFragment.ID_ARG_ACCOUNT, accountId)
                putLong(TransactionDetailsFragment.ID_ARG_TRANSACTION, transactionId)
            }
        }
    }
}