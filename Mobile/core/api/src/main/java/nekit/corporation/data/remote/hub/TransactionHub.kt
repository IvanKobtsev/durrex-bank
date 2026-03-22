package nekit.corporation.data.remote.hub

import android.annotation.SuppressLint
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import nekit.corporation.architecture.api.SignalRHub
import nekit.corporation.auth.domain.usecase.GetCredentialsUseCase
import nekit.corporation.data.di.MainServerUrl
import nekit.corporation.loan_shared.data.datasource.remote.api.AccountHub
import nekit.corporation.util.domain.common.ForbiddenFailure
import kotlinx.coroutines.rx3.rxSingle

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class TransactionHub(
    @field:MainServerUrl
    private val serverUrl: String,
    private val getCredentialsUseCase: GetCredentialsUseCase
) : AccountHub, SignalRHub<Unit, Unit>() {

    private var hubConnection: HubConnection = HubConnectionBuilder.create("$serverUrl/chatHub")
        .withAccessTokenProvider(Single.defer {
            rxSingle(exceptionHandler) {
                val token = getCredentialsUseCase()
                if (token == null) {
                    outputFlow.emit(throw ForbiddenFailure())
                }
                token.token
            }
        })
        .build()

    override fun getTransactionHubEvents() = outputFlow

    override val inputFlow = MutableStateFlow(Unit)
    override val outputFlow = MutableStateFlow(Result.success(Unit))

    override fun reconnect() {
        connect()
    }

    @SuppressLint("CheckResult")
    override fun connect() {
        hubConnection.start().subscribe(
            { println("Connected!") },
            { error -> println("Error: ${error.message}") },
        )

        hubConnection.onClosed {
            scope.launch {
                reconnect()
            }
        }
    }

    override val tag: String
        get() = TransactionHub::class.toString()
}