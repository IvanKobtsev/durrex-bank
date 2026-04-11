package nekit.corporation.shell_main_impl.navigation

import com.github.terrakok.cicerone.Screen
import dev.zacsweers.metro.Inject
import nekit.corporation.ProfileApi
import nekit.corporation.history_api.HistoryApi
import nekit.corporation.main_api.MainApi
import nekit.corporation.shell_main_api.model.Tab

@Inject
class MainBottomBarNavigator(
    private val mainBottomBarRouter: MainBottomBarRouter,
    private val mainApi: MainApi,
    private val historyApi: HistoryApi,
    private val profileApi: ProfileApi
) {

    fun toTab(tab: Tab) {
        when (tab) {
            Tab.Main -> mainBottomBarRouter.replaceScreen(mainApi.main())
            Tab.History -> mainBottomBarRouter.replaceScreen(historyApi.history())
            Tab.Profile -> mainBottomBarRouter.replaceScreen(profileApi.profile())
        }
    }

    fun back(){
        mainBottomBarRouter.exit()
    }

    fun changeScreen(screen: () -> Screen){
        mainBottomBarRouter.navigateTo(screen())
    }
}
