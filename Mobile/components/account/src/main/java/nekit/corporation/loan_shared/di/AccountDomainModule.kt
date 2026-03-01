package nekit.corporation.loan_shared.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import nekit.corporation.common.AppScope
import nekit.corporation.loan_shared.data.repository.AccountRepositoryImpl
import nekit.corporation.loan_shared.domain.repository.AccountRepository

@ContributesTo(AppScope::class)
@Module
interface AccountDomainModule {

    @Binds
    fun provideAccountRepository(impl: AccountRepositoryImpl): AccountRepository
}