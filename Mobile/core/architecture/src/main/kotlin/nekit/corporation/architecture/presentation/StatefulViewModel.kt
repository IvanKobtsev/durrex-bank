package nekit.corporation.architecture.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import nekit.corporation.architecture.R
import nekit.corporation.util.domain.common.ValidationError

abstract class StatefulViewModel<State : ScreenState> : ViewModel() {

    private val _screenState by lazy {
        MutableStateFlow(
            StateChangeUnit(
                createInitialState()
            )
        )
    }
    val screenState: StateFlow<StateChangeUnit<State>>
        get() = _screenState

    val currentScreenState: State get() = _screenState.value.currentState

    val screenEvents: EventQueue = EventQueue()

    fun updateState(transform: suspend State.() -> State) {
        viewModelScope.launch {
            val current = currentScreenState
            val new = transform.invoke(current)
            _screenState.emit(StateChangeUnit(currentState = new))
            Log.d(STATE, _screenState.value.toString())
        }
    }

    inline fun <reified S : State> updateStateOf(
        noinline transform: suspend S.() -> S
    ) {
        updateState {
            if (this is S) transform()
            else this
        }
    }

    fun offerEvent(event: Event) {
        Log.d(STATE, event.toString())
        screenEvents.offerEvent(event)
    }

    protected fun reduceValidationError(error: ValidationError?): Int? {
        return when (error) {
            ValidationError.InvalidLogin -> R.string.login_constraint
            ValidationError.InvalidRepeatPassword -> R.string.not_match_password
            ValidationError.EmptyField -> R.string.empty_field
            null -> null
            ValidationError.InvalidName -> R.string.name_constraint
            ValidationError.InvalidPhone -> R.string.phone_constraint
            ValidationError.InvalidEmail -> R.string.email_constraint
            ValidationError.InvalidSurname -> R.string.surname_constraint
        }
    }

    abstract fun createInitialState(): State

    private companion object {
        const val STATE = "state"
    }
}