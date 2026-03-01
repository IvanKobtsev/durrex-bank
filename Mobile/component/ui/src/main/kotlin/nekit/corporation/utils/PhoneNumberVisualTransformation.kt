package nekit.corporation.utils

import android.telephony.PhoneNumberUtils
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.*
import kotlin.math.max

class PhoneNumberVisualTransformation(
    countryCode: String = Locale.getDefault().country
) : VisualTransformation {

    private val phoneNumberFormatter = PhoneNumberUtil.getInstance().getAsYouTypeFormatter(countryCode)

    override fun filter(text: AnnotatedString): TransformedText {
        val transformation = reformat(text)

        return TransformedText(AnnotatedString(transformation.formatted ?: ""), object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return transformation.originalToTransformed[offset]
            }

            override fun transformedToOriginal(offset: Int): Int {
                return transformation.transformedToOriginal[offset]
            }
        })
    }

    private fun reformat(text: CharSequence): Transformation {
        phoneNumberFormatter.clear()

        var formatted: String? = getFormattedNumber(PLUS)
        var lastNonSeparator = 0.toChar()

        text.forEach { char ->
            if (PhoneNumberUtils.isNonSeparator(char)) {
                if (lastNonSeparator.code != 0) {
                    formatted = getFormattedNumber(lastNonSeparator)
                }
                lastNonSeparator = char
            }
        }
        if (lastNonSeparator.code != 0) {
            formatted = getFormattedNumber(lastNonSeparator)
        }

        return getFormattedTextTransformation(formatted)
    }

    private fun getFormattedNumber(lastNonSeparator: Char): String? =
        phoneNumberFormatter.inputDigit(lastNonSeparator)

    private fun getFormattedTextTransformation(formatted: String?): Transformation {
        val originalToTransformed = mutableListOf<Int>()
        val transformedToOriginal = mutableListOf<Int>()
        var specialCharsCount = 0
        formatted?.forEachIndexed { index, char ->
            if (PhoneNumberUtils.isNonSeparator(char) && char != PLUS) {
                originalToTransformed.add(index)
            } else {
                specialCharsCount++
            }
            transformedToOriginal.add(max(0, index - specialCharsCount))
        }
        originalToTransformed.add(originalToTransformed.maxOrNull()?.plus(1) ?: 1)
        transformedToOriginal.add(transformedToOriginal.maxOrNull()?.takeIf { it != 0 }?.plus(1) ?: 0)

        return Transformation(formatted, originalToTransformed, transformedToOriginal)
    }

    private data class Transformation(
        val formatted: String?,
        val originalToTransformed: List<Int>,
        val transformedToOriginal: List<Int>
    )

    private companion object {
        const val PLUS = '+'
    }
}
