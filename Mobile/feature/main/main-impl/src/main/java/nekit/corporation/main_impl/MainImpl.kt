package nekit.corporation.main_impl

import androidx.lifecycle.ViewModelProvider
import com.github.terrakok.cicerone.androidx.FragmentScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import nekit.corporation.main_api.MainApi
import nekit.corporation.main_impl.presentation.MainFragment

@Inject
@ContributesBinding(AppScope::class)
class MainImpl(
    private val viewModelFactory: ViewModelProvider.Factory
) : MainApi {

    override fun main() = FragmentScreen {
        MainFragment(viewModelFactory)
    }
}