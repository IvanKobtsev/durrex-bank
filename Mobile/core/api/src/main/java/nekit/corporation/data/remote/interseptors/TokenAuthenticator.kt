package nekit.corporation.data.remote.interseptors

import kotlinx.coroutines.runBlocking
import nekit.corporation.data.remote.Network.addAuthorizationHeader
import nekit.corporation.data.remote.Network.getAuthorizationHeader
import nekit.corporation.data.remote.Network.getBearerToken
import nekit.corporation.auth.domain.repository.AuthRepository
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val repository: AuthRepository
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = getRefreshToken(response.request)
        return if (accessToken != null) {
            response.request.newBuilder()
                .addAuthorizationHeader(accessToken)
                .build()
        } else {
            null
        }
    }

    @Synchronized
    private fun getRefreshToken(request: Request): String? = runBlocking {
        val token = repository.getToken() ?: return@runBlocking null
        if (getBearerToken(token.token) != request.getAuthorizationHeader()) {
            return@runBlocking token.token
        }

        val credentials = repository.getToken()

        return@runBlocking credentials?.token
    }
}
