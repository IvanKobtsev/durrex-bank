package nekit.corporation.shell_main_impl.presentation.model

import nekit.corporation.architecture.presentation.ScreenState
import nekit.corporation.shell_main_api.model.Tab

internal data class BottomBarState(
    val selectedTab: Tab
) : ScreenState
