package nekit.corporation.data.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import nekit.corporation.auth.data.datasource.remote.AuthApi
import nekit.corporation.common.AppScope
import nekit.corporation.common.BuildConfig
import nekit.corporation.data.remote.Network
import nekit.corporation.data.remote.Network.getHttpClient
import nekit.corporation.data.remote.interseptors.AuthInterceptor
import nekit.corporation.data.remote.interseptors.NetworkConnectionInterceptor
import nekit.corporation.data.remote.interseptors.StatusCodeInterceptor
import nekit.corporation.data.remote.interseptors.TokenAuthenticator
import nekit.corporation.data.remote.serializer.OffsetDateTimeSerializer
import nekit.corporation.loan_shared.data.datasource.remote.api.AccountsApi
import nekit.corporation.loan_shared.data.datasource.remote.api.LoanApi
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import java.time.OffsetDateTime

@Module
@ContributesTo(AppScope::class)
object NetworkModule {

    @Provides
    fun provideOkhttpCache(): Cache = Network.okHttpCache

    @Provides
    fun providesJson() = Json {
        serializersModule = SerializersModule {
            contextual(OffsetDateTime::class, OffsetDateTimeSerializer)
        }
        ignoreUnknownKeys = true
    }

    val contentType = "application/json".toMediaType()

    @AuthOkHttpClient
    @Provides
    fun provideOkHttpClient(
        okHttpCache: Cache,
        networkInterceptor: NetworkConnectionInterceptor,
        authenticator: TokenAuthenticator,
        authInterceptor: AuthInterceptor,
        statusCodeInterceptor: StatusCodeInterceptor
    ): OkHttpClient = getHttpClient(
        cache = okHttpCache,
        interceptors = listOf(statusCodeInterceptor, authInterceptor, networkInterceptor),
        authenticator = authenticator,
        isDebug = BuildConfig.DEBUG,
    )

    @DefaultOkHttpClient
    @Provides
    fun provideClientWithoutInterceptors(
        networkInterceptor: NetworkConnectionInterceptor,
        statusCodeInterceptor: StatusCodeInterceptor,
        okHttpCache: Cache
    ): OkHttpClient = getHttpClient(
        cache = okHttpCache,
        interceptors = listOf(networkInterceptor, statusCodeInterceptor),
        isDebug = BuildConfig.DEBUG,
    )

    @DefaultRetrofit
    @Provides
    fun provideDefaultRetrofit(
        @DefaultOkHttpClient okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://shift-courses-result.yc.ftc.ru/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(okHttpClient)
            .build()

    @AuthRetrofit
    @Provides
    fun provideAuthRetrofit(@AuthOkHttpClient okHttpClient: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://shift-courses-result.yc.ftc.ru/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(okHttpClient)
            .build()

    @Provides
    fun provideAuthService(@DefaultRetrofit retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    fun provideLoanService(@AuthRetrofit retrofit: Retrofit): LoanApi =
        retrofit.create(LoanApi::class.java)

    @Provides
    fun provideAccountService(@AuthRetrofit retrofit: Retrofit): AccountsApi =
        retrofit.create<AccountsApi>()
}
