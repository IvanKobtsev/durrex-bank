package nekit.corporation.presentation

import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.navigation.MainBottomBarRouter
import nekit.corporation.navigation.Screens.main
import nekit.corporation.navigation.Screens.menu
import nekit.corporation.presentation.model.BottomBarState
import nekit.corporation.presentation.model.MainBottomBarTabs
import javax.inject.Inject

class BottomBarViewModel @Inject constructor(
    private val mainBottomBarRouter: MainBottomBarRouter
) : StatefulViewModel<BottomBarState>() {

    override fun createInitialState(): BottomBarState {
        return BottomBarState(
            selectedTab = MainBottomBarTabs.Main
        )
    }

    fun onTabClick(tab: MainBottomBarTabs) {
        updateState {
            copy(
                selectedTab = tab
            )
        }
        when (tab) {
            MainBottomBarTabs.Main -> mainBottomBarRouter.replaceScreen(main())
            MainBottomBarTabs.Menu -> mainBottomBarRouter.replaceScreen(menu())
        }
    }
}
