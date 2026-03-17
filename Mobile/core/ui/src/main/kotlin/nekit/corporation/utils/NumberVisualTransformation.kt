package nekit.corporation.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class NumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text

        val parts = original.split('.', limit = 2)
        val integerPart = parts[0]
        val fractionalPart = if (parts.size > 1) ".${parts[1]}" else ""

        val formattedInteger = if (integerPart.isEmpty()) {
            ""
        } else {
            buildString {
                var i = integerPart.length
                while (i > 0) {
                    val start = maxOf(i - 3, 0)
                    if (isNotEmpty()) insert(0, ' ')
                    insert(0, integerPart.substring(start, i))
                    i = start
                }
            }
        }

        val formatted = formattedInteger + fractionalPart
        if (original.isEmpty()) return TransformedText(AnnotatedString(""), OffsetMapping.Identity)

        val originalLength = original.length
        val formattedLength = formatted.length

        val offsetMap = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                require(offset in 0..originalLength)

                var formattedPos = 0
                var originalPos = 0
                while (originalPos < offset) {
                    val originalChar = original[originalPos]
                    originalPos++

                    if (originalChar.isDigit()) {
                        formattedPos++

                        val remainingDigitsAfter = integerPart.length - originalPos
                        if (originalPos <= integerPart.length && remainingDigitsAfter > 0 && remainingDigitsAfter % 3 == 0) {
                            formattedPos++
                        }
                    } else {
                        formattedPos++
                    }
                }
                return formattedPos
            }

            override fun transformedToOriginal(offset: Int): Int {
                require(offset in 0..formattedLength)

                var originalPos = 0
                var formattedPos = 0
                while (formattedPos < offset) {
                    val formattedChar = formatted[formattedPos]
                    formattedPos++

                    if (formattedChar.isDigit()) {
                        originalPos++
                    } else if (formattedChar == '.') {
                        originalPos++
                    }
                }
                return originalPos
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMap)
    }
}