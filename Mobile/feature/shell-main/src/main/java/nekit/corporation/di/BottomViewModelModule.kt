package nekit.corporation.di

import androidx.lifecycle.ViewModel
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import nekit.corporation.common.AppScope
import nekit.corporation.common.ViewModelKey
import nekit.corporation.presentation.BottomBarViewModel

@Module
@ContributesTo(AppScope::class)
interface BottomViewModelModule {

    @Binds
    @ViewModelKey(BottomBarViewModel::class)
    @IntoMap
    fun bottomBarViewModel(impl: BottomBarViewModel): ViewModel
}