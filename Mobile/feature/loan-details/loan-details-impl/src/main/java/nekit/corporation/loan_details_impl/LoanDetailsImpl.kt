package nekit.corporation.loan_details_impl

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.github.terrakok.cicerone.androidx.FragmentScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import nekit.corporation.loan_details_api.LoanDetailsApi
import nekit.corporation.loan_details_impl.presentation.LoanDetailsFragment

@Inject
@ContributesBinding(AppScope::class)
class LoanDetailsImpl(
    val viewModelFactory: ViewModelProvider.Factory
) : LoanDetailsApi {

    override fun loanDetails(id: Int) = FragmentScreen {
        LoanDetailsFragment(viewModelFactory).apply {
            arguments = Bundle().apply {
                putInt(LoanDetailsFragment.ID_ARG, id)
            }
        }
    }
}