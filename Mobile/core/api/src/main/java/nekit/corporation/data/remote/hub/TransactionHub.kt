package nekit.corporation.data.remote.hub

import android.annotation.SuppressLint
import android.util.Log
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import nekit.corporation.architecture.api.SignalRHub
import nekit.corporation.auth.domain.usecase.GetCredentialsUseCase
import nekit.corporation.loan_shared.data.datasource.remote.api.AccountHub
import nekit.corporation.util.domain.common.ForbiddenFailure
import kotlinx.coroutines.rx3.rxSingle
import nekit.corporation.common.MainServerUrl
import nekit.corporation.loan_shared.data.datasource.remote.model.TransactionResponse

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, binding = binding<AccountHub>())
class TransactionHub(
    @param:MainServerUrl
    private val serverUrl: String,
    private val getCredentialsUseCase: GetCredentialsUseCase
) : AccountHub, SignalRHub<Unit, Unit>() {

    private var currentAccountId: Int? = null

    private val hubConnection: HubConnection = HubConnectionBuilder
        .create("$serverUrl$ENDPOINT")
        .withAccessTokenProvider(Single.defer {
            rxSingle(exceptionHandler) {
                val token = getCredentialsUseCase()
                if (token == null) {
                    outputFlow.emit(Result.failure(ForbiddenFailure()))
                }
                token?.token.toString()
            }
        })
        .build()

    override val inputFlow = MutableStateFlow(Unit)
    override val outputFlow = MutableStateFlow(Result.success(Unit))

    override fun getTransactionHubEvents() = outputFlow

    fun subscribeToAccount(accountId: Int) {
        currentAccountId = accountId
        hubConnection.invoke(
            "SubscribeToAccount",
            accountId
        )
    }

    override fun reconnect() {
        connect()
        currentAccountId?.let { subscribeToAccount(it) }
    }

    @SuppressLint("CheckResult")
    override fun connect() {
        if (hubConnection.connectionState.name == "CONNECTED") return

        hubConnection.on("Subscribed", { accountId: Int ->
            Log.d(tag, "Subscribed to account $accountId")
            scope.launch {
                outputFlow.emit(Result.success(Unit))
            }
        }, Int::class.java)

        hubConnection.on("Error", { message: String ->
            Log.d(tag, "Failure $message")
            scope.launch {
                outputFlow.emit(Result.failure(Throwable(message)))
            }

        }, String::class.java)

        hubConnection.on("NewTransaction", { tx: TransactionResponse ->
            Log.d(tag, "success: $tx")
            outputFlow.value = Result.success(Unit)
        }, TransactionResponse::class.java)

        hubConnection.onClosed {
            scope.launch {
                delay(1000)
                reconnect()
            }
        }

        hubConnection.start().subscribe(
            { println("Connected!") },
            { error -> println("Error: ${error.message}") }
        )
    }

    override val tag: String
        get() = TransactionHub::class.toString()

    companion object {
        private const val ENDPOINT = "/hubs/transactions"
    }
}
