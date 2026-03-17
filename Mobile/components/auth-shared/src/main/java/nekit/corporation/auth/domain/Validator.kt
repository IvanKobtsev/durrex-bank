package nekit.corporation.auth.domain

import nekit.corporation.util.domain.common.ValidationError
import nekit.corporation.utils.PhoneNumberUtils.isValidNumber

object Validator {

    fun validateLogin(login: String): ValidationError? {
        return if (login.length > 3) null else ValidationError.InvalidLogin

    }

    fun validateEmptyField(field: String): ValidationError? {
        return if (field.isBlank()) ValidationError.EmptyField else null
    }

    fun validateRepeatPassword(password: String, repeatPassword: String): ValidationError? {
        return if (repeatPassword == password) null else ValidationError.InvalidRepeatPassword
    }

    fun phoneValidator(phone: String): ValidationError? {
        return if (!isValidNumber(phone)) ValidationError.InvalidPhone else null

    }

    fun emailValidator(email: String): ValidationError? {
        val emailRegex = Regex(
            """^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$""",
            setOf(RegexOption.IGNORE_CASE)
        )
        return if (emailRegex.matches(email)) null else ValidationError.InvalidEmail
    }

    fun firstNameValidator(firstName: String): ValidationError? {
        return if (firstName.isEmpty()) ValidationError.InvalidName else null
    }

    fun lastNameValidator(lastName: String): ValidationError? {
        return if (lastName.isEmpty()) ValidationError.InvalidSurname else null
    }
}