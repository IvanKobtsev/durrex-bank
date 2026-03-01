package nekit.corporation.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import nekit.corporation.presentation.MainFragment
import nekit.corporation.presentation.menu.HistoryFragment

object Screens {

    fun main() = FragmentScreen {
        MainFragment()
    }

    fun menu() = FragmentScreen {
        HistoryFragment()
    }
}