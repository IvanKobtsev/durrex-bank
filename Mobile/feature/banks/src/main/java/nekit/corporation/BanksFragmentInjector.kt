package nekit.corporation

import nekit.corporation.presentation.BanksFragment

interface BanksFragmentInjector {

    fun inject(fragment: BanksFragment)
}