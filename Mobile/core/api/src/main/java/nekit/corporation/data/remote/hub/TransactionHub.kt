package nekit.corporation.data.remote.hub

import android.annotation.SuppressLint
import android.util.Log
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.rxSingle
import nekit.corporation.architecture.api.SignalRHub
import nekit.corporation.auth.domain.usecase.GetCredentialsUseCase
import nekit.corporation.common.MainServerUrl
import nekit.corporation.loan_shared.data.datasource.remote.api.AccountHub
import nekit.corporation.loan_shared.data.datasource.remote.model.TransactionResponse
import nekit.corporation.util.domain.common.ForbiddenFailure
import java.util.logging.Level
import java.util.logging.Logger

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, binding = binding<AccountHub>())
class TransactionHub(
    @param:MainServerUrl
    private val serverUrl: String,
    private val getCredentialsUseCase: GetCredentialsUseCase,
) : AccountHub, SignalRHub<Unit, Unit>() {

    private var currentAccountId: Int? = null

    private val hubConnection: HubConnection = HubConnectionBuilder
        .create("${serverUrl.trimEnd('/')}/$ENDPOINT")
        .withAccessTokenProvider(Single.defer {
            rxSingle(exceptionHandler) {
                val token = getCredentialsUseCase()
                if (token == null) {
                    outputFlow.emit(Result.failure(ForbiddenFailure()))
                    throw ForbiddenFailure()
                }
                token.token
            }
        })
        .build()

    override val inputFlow = MutableStateFlow(Unit)
    override val outputFlow = MutableSharedFlow<Result<Unit>>()

    override fun getTransactionHubEvents(accountId: Int): Flow<Result<Unit>> {
        currentAccountId = accountId
        connect()
        return outputFlow
    }

    @SuppressLint("CheckResult")
    fun subscribeToAccount(accountId: Int) {
        currentAccountId = accountId
        Log.d(
            tag,
            "Attempting to subscribe to account $accountId. State: ${hubConnection.connectionState}"
        )

        if (hubConnection.connectionState != HubConnectionState.CONNECTED) {
            Log.w(tag, "Cannot subscribe: Connection state is ${hubConnection.connectionState}")
            return
        }

        hubConnection.invoke("SubscribeToAccount", accountId)
            .subscribe(
                { Log.d(tag, "Subscribe request sent for account $accountId") },
                { error -> Log.e(tag, "Subscribe failed", error) }
            )
    }

    override fun reconnect() {
        Log.d(tag, "reconnecting...")
        connect()
    }

    @SuppressLint("CheckResult")
    override fun connect() {
        Log.d(tag, "connect() called. Current state: ${hubConnection.connectionState}")

        if (hubConnection.connectionState == HubConnectionState.CONNECTED) return

        if (!handlersRegistered) {
            handlersRegistered = true
            hubConnection.on("EventEnvelope", { payload: MutableMap<String?, Any?>? ->
                when (val type = payload!!["type"] as String) {
                    "Message" -> {
                        Log.w(tag, "Message ${payload.entries}")
                    }

                    else -> Log.w(tag, "Unknown event type: $type")
                }
            }, MutableMap::class.java)
            hubConnection.on("Subscribed", { accountId: Int ->
                Log.d(tag, "Subscribed event received from server for account $accountId")
                scope.launch {
                    outputFlow.emit(Result.success(Unit))
                }
            }, Int::class.java)

            hubConnection.on("Error", { message: String ->
                Log.e(tag, "Server-side hub error: $message")
                scope.launch {
                    outputFlow.emit(Result.failure(Throwable(message)))
                    delay(500)
                    reconnect()
                }
            }, String::class.java)

            hubConnection.on("NewTransaction", { tx: Any ->
                Log.d(tag, "New transaction received: $tx")
                scope.launch {
                    outputFlow.emit(Result.success(Unit))
                }
            }, Any::class.java)

            hubConnection.onClosed {
                Log.d(tag, "Connection closed. Reconnecting in 2s...")
                scope.launch {
                    delay(2000)
                    reconnect()
                }
            }
        }

        hubConnection.start().subscribe(
            {
                Log.d(tag, "Connected successfully to SignalR hub!")
                currentAccountId?.let {
                    subscribeToAccount(it)
                }
            },
            { error ->
                Log.e(tag, "Connection start error (handshake failed)", error)
            }
        )
    }

    override val tag: String
        get() = "TransactionHub"
    private var handlersRegistered = false

    companion object {
        private const val ENDPOINT = "core/hubs/transactions"
    }
}
