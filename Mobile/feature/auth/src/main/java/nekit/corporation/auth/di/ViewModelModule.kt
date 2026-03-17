package nekit.corporation.auth.di

import androidx.lifecycle.ViewModel
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import nekit.corporation.auth.presentation.auth.AuthViewModel
import nekit.corporation.common.AppScope
import nekit.corporation.common.ViewModelKey

@Module
@ContributesTo(AppScope::class)
interface ViewModelModule {

    @Binds
    @ViewModelKey(AuthViewModel::class)
    @IntoMap
    fun authViewModel(impl: AuthViewModel): ViewModel
}