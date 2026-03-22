package nekit.corporation.create_loan_impl.navigation

import com.github.terrakok.cicerone.Router
import dev.zacsweers.metro.Inject

@Inject
class CreateCreditNavigator(
    private val router: Router
) {

    fun onBack() = router.exit()
}