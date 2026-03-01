package nekit.corporation.data.remote.interseptors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import nekit.corporation.data.remote.Network.addAuthorizationHeader
import nekit.corporation.auth.domain.repository.AuthRepository
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val repository: AuthRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = getRequestWithHeaders(request)

        return getResponseFromRequest(chain, request)
    }

    private fun getRequestWithHeaders(request: Request): Request {
        val requestBuilder = request.newBuilder()

        runBlocking(Dispatchers.IO) {
            val token = repository.getToken()?.token

            if (token != null)
                requestBuilder
                    .addAuthorizationHeader(token)
        }

        return requestBuilder.build()
    }

    @Suppress("SwallowedException")
    private fun getResponseFromRequest(
        chain: Interceptor.Chain,
        requestWithHeaders: Request
    ): Response =
        chain.proceed(requestWithHeaders)
}
