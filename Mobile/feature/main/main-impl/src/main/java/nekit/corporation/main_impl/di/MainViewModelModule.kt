package nekit.corporation.di

import androidx.lifecycle.ViewModel
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import nekit.corporation.common.AppScope
import nekit.corporation.common.ViewModelKey
import nekit.corporation.presentation.MainViewModel

@Module
@ContributesTo(AppScope::class)
interface MainViewModelModule {

    @Binds
    @ViewModelKey(MainViewModel::class)
    @IntoMap
    fun mainViewModel(impl: MainViewModel): ViewModel
}
