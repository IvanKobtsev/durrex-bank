package nekit.corporation.util.domain.common

sealed interface ValidationError {

    data object InvalidLogin : ValidationError


    data object InvalidName : ValidationError

    data object InvalidSurname : ValidationError

    data object InvalidEmail : ValidationError

    data object InvalidPhone : ValidationError

    data object InvalidRepeatPassword : ValidationError

    data object EmptyField : ValidationError
}