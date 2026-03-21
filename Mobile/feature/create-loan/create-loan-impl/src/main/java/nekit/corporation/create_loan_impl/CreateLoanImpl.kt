package nekit.corporation.create_loan_impl

import androidx.lifecycle.ViewModelProvider
import com.github.terrakok.cicerone.androidx.FragmentScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import nekit.corporation.create_loan_api.CreateLoanApi
import nekit.corporation.create_loan_impl.presentation.CreateCreditFragment

@Inject
@ContributesBinding(AppScope::class)
class CreateLoanImpl(val viewModelFactory: ViewModelProvider.Factory) : CreateLoanApi {

    override fun createLoan() = FragmentScreen {
        CreateCreditFragment(viewModelFactory)
    }
}