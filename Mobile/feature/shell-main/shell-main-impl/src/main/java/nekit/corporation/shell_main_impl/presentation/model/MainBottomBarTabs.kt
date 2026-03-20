package nekit.corporation.presentation.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import nekit.corporation.shell_main.R

enum class MainBottomBarTabs(
    @param:StringRes val nameRes: Int,
    @param:DrawableRes val iconRes: Int
) {
    Main(R.string.main, R.drawable.home_ic),
    Menu(R.string.history, R.drawable.menu_ic),
    Profile(R.string.profile, R.drawable.account_ic)
}