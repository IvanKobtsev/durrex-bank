package nekit.corporation.user.data.di

import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.optional.SingleIn
import dagger.Binds
import dagger.Module
import dagger.Provides
import nekit.corporation.common.AppScope
import nekit.corporation.user.data.repository.UserRepositoryImpl
import nekit.corporation.user.domain.UserRepository

@Module
@ContributesTo(AppScope::class)
@SingleIn(AppScope::class)
abstract class UserModule {

    @Binds
    abstract fun provideUserRepository(impl: UserRepositoryImpl): UserRepository
}