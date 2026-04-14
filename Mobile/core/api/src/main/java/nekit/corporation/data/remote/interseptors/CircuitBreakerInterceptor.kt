@file:OptIn(ExperimentalAtomicApi::class)

package nekit.corporation.data.remote.interseptors

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import nekit.corporation.util.domain.common.CircuitBreakerOpenFailure
import okhttp3.Interceptor
import okhttp3.Response
import nekit.corporation.util.domain.common.ServerFailure
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.read
import kotlin.concurrent.write
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.atomics.ExperimentalAtomicApi

//TODO переделать выделив логику хранения
@Inject
@SingleIn(AppScope::class)
class CircuitBreakerInterceptor(
    context: Context
) : Interceptor {

    private val prefs: SharedPreferences = context.getSharedPreferences("circuit_breaker", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        val openUntil = getOpenUntil()
        val now = Instant.now()

        if (openUntil != null && now.isBefore(openUntil)) {
            Log.d(TAG, "Circuit breaker is OPEN until $openUntil")
            throw CircuitBreakerOpenFailure("Circuit breaker is OPEN")
        }

        return try {
            val response = chain.proceed(chain.request())

            if (response.code == 503) {
                handleFailure()
                response.close()
                throw CircuitBreakerOpenFailure("Circuit opened due to HTTP 503")
            }
            response
        } catch (e: Exception) {
            if (shouldOpenCircuit(e)) {
                handleFailure()
            }
            throw e
        }
    }

    private fun shouldOpenCircuit(throwable: Throwable): Boolean {
        return when (throwable) {
            is ConnectException,
            is SocketTimeoutException,
            is UnknownHostException -> true
            is IOException -> throwable.message?.contains("refused") == true
            else -> false
        }
    }

    private fun handleFailure() {
        val openUntil = Instant.now().plusSeconds(TIMEOUT_SECONDS)
        prefs.edit().putString(KEY_OPEN_UNTIL, openUntil.toString()).apply()
        Log.d(TAG, "Circuit opened until $openUntil")
    }

    private fun getOpenUntil(): Instant? {
        val timestamp = prefs.getString(KEY_OPEN_UNTIL, null) ?: return null
        return try {
            Instant.parse(timestamp)
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val TAG = "CircuitBreakerInterceptor"
        private const val KEY_OPEN_UNTIL = "open_until"
        private const val TIMEOUT_SECONDS = 30L
    }
}