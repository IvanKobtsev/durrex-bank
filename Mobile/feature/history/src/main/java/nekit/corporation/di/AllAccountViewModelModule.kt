package nekit.corporation.di

import androidx.lifecycle.ViewModel
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import nekit.corporation.common.AppScope
import nekit.corporation.common.ViewModelKey
import nekit.corporation.presentation.menu.HistoryViewModel

@Module
@ContributesTo(AppScope::class)
interface AllAccountViewModelModule {

    @Binds
    @ViewModelKey(HistoryViewModel::class)
    @IntoMap
    fun menuViewModel(impl: HistoryViewModel): ViewModel
}
