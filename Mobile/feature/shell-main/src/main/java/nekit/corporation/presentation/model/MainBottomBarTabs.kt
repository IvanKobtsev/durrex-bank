package nekit.corporation.presentation.model

sealed interface MainBottomBarTabs {

    data object Main : MainBottomBarTabs

    data object Menu : MainBottomBarTabs
}
