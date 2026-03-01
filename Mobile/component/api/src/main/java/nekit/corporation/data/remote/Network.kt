package nekit.corporation.data.remote

import okhttp3.Authenticator
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.collections.forEach

object Network {
    val okHttpCache: Cache
        get() {
            val cacheDirectory = File("http-cache.tmp")
            val cacheSize = CACHE_SIZE
            return Cache(cacheDirectory, cacheSize.toLong())
        }
    fun Request.getAuthorizationHeader(): String? = header(AUTHORIZATION)

    fun Request.Builder.addAuthorizationHeader(token: String): Request.Builder =
        header(AUTHORIZATION, getBearerToken(token))

    fun getBearerToken(token: String): String =
        "$BEARER $token"

    fun getHttpClient(
        cache: Cache,
        interceptors: List<Interceptor> = listOf(),
        authenticator: Authenticator? = null,
        isDebug: Boolean
    ): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder().apply {
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            cache(cache)
            interceptors.forEach { addInterceptor(it) }
            authenticator?.let { authenticator(it) }
            if (isDebug) {
                val logLevel = HttpLoggingInterceptor.Level.BODY
                addInterceptor(HttpLoggingInterceptor().setLevel(logLevel))
            }
        }
        return httpClientBuilder.build()
    }

    private const val CONNECT_TIMEOUT = 15L
    private const val READ_TIMEOUT = 60L
    private const val WRITE_TIMEOUT = 30L
    private const val BEARER = "Bearer"
    private const val AUTHORIZATION = "Authorization"
    private const val CACHE_SIZE = 50 * 1024 * 1024
}
