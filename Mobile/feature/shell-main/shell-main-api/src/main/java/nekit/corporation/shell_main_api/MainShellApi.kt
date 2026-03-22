package nekit.corporation.shell_main_api

import com.github.terrakok.cicerone.Screen
import nekit.corporation.shell_main_api.model.Tab

interface MainShellApi {

    fun onTab(tab: Tab): Screen

    fun back()

    fun runScreen(screen: () -> Screen)
}