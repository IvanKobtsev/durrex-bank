package nekit.corporation.auth.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import nekit.corporation.auth.data.repository.AuthRepositoryImpl
import nekit.corporation.auth.domain.repository.AuthRepository
import nekit.corporation.common.AppScope


@ContributesTo(AppScope::class)
@Module
interface DomainModule {

    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
