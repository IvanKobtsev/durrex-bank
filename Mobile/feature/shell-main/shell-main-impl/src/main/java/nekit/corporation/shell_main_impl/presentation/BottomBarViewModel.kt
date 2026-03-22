package nekit.corporation.shell_main_impl.presentation

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.shell_main_api.model.Tab
import nekit.corporation.shell_main_impl.navigation.MainBottomBarNavigator
import nekit.corporation.shell_main_impl.presentation.model.BottomBarState

@Inject
@ViewModelKey(BottomBarViewModel::class)
@ContributesIntoMap(AppScope::class)
internal class BottomBarViewModel(
    private val mainBottomBarNavigator: MainBottomBarNavigator
) : StatefulViewModel<BottomBarState>() {

    override fun createInitialState(): BottomBarState {
        return BottomBarState(
            selectedTab = Tab.Main
        )
    }

    fun onTabClick(tab: Tab) {
        updateState {
            copy(
                selectedTab = tab
            )
        }
        mainBottomBarNavigator.toTab(tab)
    }
}
