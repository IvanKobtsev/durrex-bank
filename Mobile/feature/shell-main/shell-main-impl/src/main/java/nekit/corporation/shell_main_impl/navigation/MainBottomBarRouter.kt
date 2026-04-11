package nekit.corporation.shell_main_impl.navigation

import com.github.terrakok.cicerone.Router
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
class MainBottomBarRouter : Router()