package nekit.corporation.user.data.di

import com.squareup.anvil.annotations.optional.SingleIn
import dagger.Module
import dagger.Provides
import nekit.corporation.common.AppScope
import nekit.corporation.user.data.repository.UserRepositoryImpl
import nekit.corporation.user.domain.UserRepository

@Module
@SingleIn(AppScope::class)
object UserModule {

    @Provides
    fun provideUserRepository(impl: UserRepositoryImpl): UserRepository = impl
}