@file:OptIn(ExperimentalAtomicApi::class)

package nekit.corporation.data.remote.interseptors

import dev.zacsweers.metro.Inject
import nekit.corporation.data.remote.model.CircuitBreakerState
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.read
import kotlin.concurrent.write

@Inject
class CircuitBreakerInterceptor() : Interceptor {

    private val state = AtomicReference(CircuitBreakerState.CLOSED)
    private val ringBuffer = BooleanArray(WINDOW_SIZE)
    private var index = 0
    private var requestCount = 0

    private val lock = ReentrantReadWriteLock()

    private val lastOpenTime = AtomicLong(0)

    private val halfOpenAttempts = AtomicInteger(0)

    override fun intercept(chain: Interceptor.Chain): Response {
        when (state.load()) {
            CircuitBreakerState.OPEN -> {
                val timeInOpen = System.currentTimeMillis() - lastOpenTime.load()
                if (timeInOpen >= TIMEOUT_MS) {
                    state.store(CircuitBreakerState.HALF_OPEN)
                    halfOpenAttempts.set(0)
                } else {
                    throw IOException("Circuit breaker is OPEN (${FAILURE_THRESHOLD_PERCENT}% failures threshold exceeded)")
                }
            }

            CircuitBreakerState.HALF_OPEN -> {
                val attempts = halfOpenAttempts.incrementAndGet()
                if (attempts > HALF_OPEN_MAX_ATTEMPTS) {
                    throw IOException("Circuit breaker HALF_OPEN: max attempts exceeded")
                }
            }

            CircuitBreakerState.CLOSED -> Unit
        }

        return try {
            val response = chain.proceed(chain.request())
            val success = response.isSuccessful && response.code < 500
            if (success) {
                recordResult(true)
                response
            } else {
                recordResult(false)
                throw IOException("Server error: ${response.code}")
            }
        } catch (e: IOException) {
            recordResult(false)
            throw e
        }
    }

    private fun recordResult(success: Boolean) {
        lock.write {
            ringBuffer[index] = success
            index = (index + 1) % WINDOW_SIZE
            if (requestCount < WINDOW_SIZE) requestCount++
        }

        if (state.load() == CircuitBreakerState.CLOSED) {
            val currentFailureRate = getCurrentFailureRate()
            if (currentFailureRate >= FAILURE_THRESHOLD_PERCENT && requestCount >= MIN_REQUESTS_TO_EVALUATE) {
                state.store(CircuitBreakerState.OPEN)
                lastOpenTime.store(System.currentTimeMillis())
            }
        } else if (state.load() == CircuitBreakerState.HALF_OPEN) {
            if (success) {
                state.store(CircuitBreakerState.CLOSED)
                clearStats()
            } else {
                state.store(CircuitBreakerState.OPEN)
                lastOpenTime.store(System.currentTimeMillis())
            }
        }
    }

    private fun getCurrentFailureRate(): Double {
        lock.read {
            if (requestCount == 0) return 0.0
            var failures = 0
            for (i in 0 until minOf(requestCount, WINDOW_SIZE)) {
                if (!ringBuffer[i]) failures++
            }
            return (failures.toDouble() / requestCount) * 100.0
        }
    }

    private fun clearStats() {
        lock.write {
            ringBuffer.fill(false)
            requestCount = 0
            index = 0
        }
    }

    private companion object {
        private const val WINDOW_SIZE: Int = 100
        private const val FAILURE_THRESHOLD_PERCENT: Double = 70.0
        private const val MIN_REQUESTS_TO_EVALUATE: Int = 20
        private const val TIMEOUT_MS: Long = 30_000
        private const val HALF_OPEN_MAX_ATTEMPTS: Int = 3
    }
}