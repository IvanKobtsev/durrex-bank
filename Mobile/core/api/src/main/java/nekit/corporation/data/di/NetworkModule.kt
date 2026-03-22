package nekit.corporation.data.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import nekit.corporation.auth.data.datasource.remote.AuthApi
import nekit.corporation.data.remote.Network
import nekit.corporation.data.remote.Network.getHttpClient
import nekit.corporation.data.remote.interseptors.AuthInterceptor
import nekit.corporation.data.remote.interseptors.NetworkConnectionInterceptor
import nekit.corporation.data.remote.interseptors.StatusCodeInterceptor
import nekit.corporation.data.remote.interseptors.TokenAuthenticator
import nekit.corporation.data.remote.serializer.InstantSerializer
import nekit.corporation.data.remote.serializer.OffsetDateTimeSerializer
import nekit.corporation.loan_shared.data.datasource.remote.api.AccountsApi
import nekit.corporation.loan_shared.data.datasource.remote.api.LoanApi
import nekit.corporation.tariff.data.remote.TariffApi
import nekit.corporation.user.data.remote.SettingsApi
import nekit.corporation.user.data.remote.UserApi
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import java.time.Instant
import java.time.OffsetDateTime

@ContributesTo(AppScope::class)
interface NetworkModule {

    @Provides
    fun provideOkhttpCache(): Cache = Network.okHttpCache

    @Provides
    fun providesJson(): Json = Json {
        serializersModule = SerializersModule {
            contextual(OffsetDateTime::class, OffsetDateTimeSerializer)
            contextual(Instant::class, InstantSerializer)
        }
        ignoreUnknownKeys = true
    }


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
    )

    @DefaultRetrofit
    @Provides
    fun provideDefaultRetrofit(
        @DefaultOkHttpClient okHttpClient: OkHttpClient,
        json: Json,
        @MainServerUrl serverUrl: String
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(serverUrl)
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(okHttpClient)
            .build()

    @AuthRetrofit
    @Provides
    fun provideAuthRetrofit(
        @AuthOkHttpClient okHttpClient: OkHttpClient, json: Json,
        @MainServerUrl serverUrl: String
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(serverUrl)
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

    @Provides
    fun provideUserService(@AuthRetrofit retrofit: Retrofit): UserApi =
        retrofit.create<UserApi>()

    @Provides
    fun provideTariffService(@AuthRetrofit retrofit: Retrofit): TariffApi =
        retrofit.create<TariffApi>()

    @Provides
    fun provideSettingsService(@AuthRetrofit retrofit: Retrofit): SettingsApi =
        retrofit.create<SettingsApi>()

    @Provides
    @MainServerUrl
    fun provideMainServerUrl(): String = "https://1bc7-66-234-150-130.ngrok-free.app/"

    companion object {
        private val contentType = "application/json".toMediaType()
    }
}
