package nekit.corporation.shell_main_api.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import nekit.corporation.shell_main_api.R

enum class Tab(
    @param:StringRes val nameRes: Int,
    @param:DrawableRes val iconRes: Int
) {
    Main(R.string.main, R.drawable.home_ic),
    History(R.string.history, R.drawable.history_ic),
    Profile(R.string.profile, R.drawable.account_ic)
}
