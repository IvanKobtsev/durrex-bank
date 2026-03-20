package nekit.corporation.presentation.model

import nekit.corporation.architecture.presentation.ScreenState

data class BottomBarState(
    val selectedTab: MainBottomBarTabs
) : ScreenState
