package nekit.corporation.data.remote.interseptors

import dev.zacsweers.metro.Inject
import kotlinx.coroutines.runBlocking
import nekit.corporation.data.remote.Network.addAuthorizationHeader
import nekit.corporation.data.remote.Network.getAuthorizationHeader
import nekit.corporation.data.remote.Network.getBearerToken
import nekit.corporation.auth.domain.repository.AuthRepository
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator @Inject constructor(
    private val repository: AuthRepository
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        runBlocking {
            repository.cleanToken()
        }
        return null
    }
}
