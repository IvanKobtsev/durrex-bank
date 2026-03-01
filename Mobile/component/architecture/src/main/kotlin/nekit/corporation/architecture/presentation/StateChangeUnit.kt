package nekit.corporation.architecture.presentation

data class StateChangeUnit<State : ScreenState>(
  val currentState: State
)