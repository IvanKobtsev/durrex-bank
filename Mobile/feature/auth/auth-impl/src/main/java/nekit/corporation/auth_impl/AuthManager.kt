package nekit.corporation.auth_impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import nekit.corporation.common.di.MainServerUrl
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.*
import androidx.core.net.toUri
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Inject
@SingleIn(AppScope::class)
class AuthManager(
    @param:MainServerUrl
    private val mainServerUrl: String,
    private val context: Context
) {

    private var authService: AuthorizationService? = null
    private var authState: AuthState = AuthState()
    private var logoutRequest: EndSessionRequest? = null
    fun initialize(onReady: () -> Unit, onError: (Exception) -> Unit) {
        val issuerUri = (mainServerUrl + "auth").toUri()
        AuthorizationServiceConfiguration.fetchFromIssuer(issuerUri) { config, ex ->
            if (config != null) {
                authState = AuthState(config)
                val appAuthConfig = AppAuthConfiguration.Builder()
                    .setSkipIssuerHttpsCheck(true)
                    .build()
                authService = AuthorizationService(context, appAuthConfig)
                onReady()

            } else {
                onError(ex ?: Exception("Discovery failed"))
            }
        }
    }

    @Synchronized
    fun logoutIntent(): Intent? {
        val config = authState.authorizationServiceConfiguration ?: return null
        if (logoutRequest == null) {
            logoutRequest = EndSessionRequest.Builder(config)
                .setIdTokenHint(authState.idToken)
                .setPostLogoutRedirectUri(AuthConfig.REDIRECT_URI.toUri())
                .build()
        }
        Log.d(TAG, "logoutRequest: ${logoutRequest?.toUri()}")
        return logoutRequest?.let {
            authService?.getEndSessionRequestIntent(it)
        }
    }

    fun buildLoginIntent(): Intent? {
        val config = authState.authorizationServiceConfiguration ?: return null
        val service = authService ?: return null

        val request = AuthorizationRequest.Builder(
            config,
            AuthConfig.CLIENT_ID,
            ResponseTypeValues.CODE,
            AuthConfig.REDIRECT_URI.toUri()
        )
            .setScope(AuthConfig.SCOPE)
            .build()

        return service.getAuthorizationRequestIntent(request)
    }

    fun handleAuthResponse(
        intent: Intent,
        onSuccess: (accessToken: String, idToken: String?, refreshToken: String?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val response = AuthorizationResponse.fromIntent(intent)
        val exception = AuthorizationException.fromIntent(intent)

        authState.update(response, exception)

        val service = authService
        if (response != null && service != null) {
            service.performTokenRequest(response.createTokenExchangeRequest()) { tokenResponse, tokenEx ->
                authState.update(tokenResponse, tokenEx)

                if (tokenResponse != null) {
                    onSuccess(
                        tokenResponse.accessToken!!,
                        tokenResponse.idToken,
                        tokenResponse.refreshToken
                    )
                } else {
                    onError(tokenEx ?: Exception("Token exchange failed"))
                }
            }
        } else {
            onError(exception ?: Exception("Authorization failed or service not initialized"))
        }
    }

    private companion object {
        private const val TAG = "AuthManager"
    }
}
