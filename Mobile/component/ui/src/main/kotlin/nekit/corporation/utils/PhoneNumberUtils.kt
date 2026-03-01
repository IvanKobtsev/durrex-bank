package nekit.corporation.utils

import androidx.compose.ui.text.AnnotatedString
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.Locale

object PhoneNumberUtils {
    private val phoneUtil = PhoneNumberUtil.getInstance()
  fun getFilteredPhone(original: String): String =
    original.filter { it.isDigit() }


    fun isValidNumber(phone: String, countryCode: String = Locale.getDefault().country): Boolean {
        return try {
            val number = phoneUtil.parse(phone, countryCode)
            phoneUtil.isValidNumber(number)
        } catch (_: NumberParseException) {
            false
        }
    }

    /*
    input: XXXXXXXXXXX or +XXXXXXXXXXX or +X XXX XXX XX-XX or any other phone number
    output: +X XXX XXX XX-XX
    Added '+' and separators. Separators depend only on input
   */
  fun getTransformedPhone(phone: String): String =
    PhoneNumberVisualTransformation().filter(AnnotatedString(phone.filter { it.isDigit() })).text.text
}