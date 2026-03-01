package nekit.corporation.domain

import nekit.corporation.util.domain.common.ValidationError
import nekit.corporation.utils.PhoneNumberUtils

object Validator {

    fun validateName(name: String): ValidationError? {
        return if (name.all { it.isLetterOrDigit() } &&
            !name.contains(Regex("[a-zA-Z0-9]"))
        ) {
            null
        } else {
            ValidationError.InvalidName
        }
    }

    fun validatePhone(phone: String): ValidationError? {
        val filteredPhone = PhoneNumberUtils.getFilteredPhone(phone)

        if (filteredPhone.isEmpty()) {
            return ValidationError.EmptyField
        }
        if (!filteredPhone.all { it.isDigit() } || filteredPhone.length !in 7..14)
            return ValidationError.InvalidPhone
        return null
    }

    fun validateEmptyField(field: String): ValidationError? {
        return if (field.isBlank()) ValidationError.EmptyField else null
    }
}