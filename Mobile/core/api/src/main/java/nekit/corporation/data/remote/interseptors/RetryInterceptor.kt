package nekit.corporation.data.remote.interseptors

import dev.zacsweers.metro.Inject
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.UUID
import kotlin.math.pow

@Inject
class RetryInterceptor : Interceptor {

    private val maxRetries: Int = 3
    private val baseDelayMs: Long = 300L

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val request = if (originalRequest.method.needsIdempotencyKey()) {
            val key = originalRequest.header("Idempotency-Key")
                ?: UUID.randomUUID().toString()

            originalRequest.newBuilder()
                .header("Idempotency-Key", key)
                .build()
        } else {
            originalRequest
        }

        var attempt = 0
        var lastIOException: IOException? = null

        while (true) {
            try {
                val response = chain.proceed(request)

                if (response.isSuccessful) return response

                if (!response.code.isRetryableStatusCode()) {
                    return response
                }

                response.close()
            } catch (e: IOException) {
                lastIOException = e
                if (!e.isRetryableException()) throw e
            }

            if (attempt >= maxRetries) {
                throw lastIOException ?: IOException("Request failed after $maxRetries retries")
            }

            val delayMs = (baseDelayMs * 2.0.pow(attempt.toDouble())).toLong()
            Thread.sleep(delayMs)
            attempt++
        }
    }

    private fun Int.isRetryableStatusCode(): Boolean {
        return this == 429 || this in 500..599
    }

    private fun IOException.isRetryableException(): Boolean {
        return this is SocketTimeoutException || this is UnknownHostException
    }

    private fun String.needsIdempotencyKey(): Boolean {
        return this == "POST" ||
                this == "PUT" ||
                this == "DELETE" ||
                this == "PATCH"
    }
}