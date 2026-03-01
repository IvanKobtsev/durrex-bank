package nekit.corporation.di

import nekit.corporation.presentation.ShellMainHostFragment

interface MainBottomBarInjector {

    fun inject(fragment: ShellMainHostFragment)
}