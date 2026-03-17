package nekit.corporation.loan_shared.di

import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.optional.SingleIn
import dagger.Binds
import dagger.Module
import nekit.corporation.common.AppScope
import nekit.corporation.loan_shared.data.repository.CreditRepositoryImpl
import nekit.corporation.loan_shared.domain.repository.CreditRepository

@ContributesTo(AppScope::class)
@SingleIn(AppScope::class)
@Module
interface CreditModule {

    @Binds
    fun provideLoanRepository(impl: CreditRepositoryImpl): CreditRepository
}