package nekit.corporation.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class NumberVisualTransformation(
    private val separator: Char = ' ',
    private val groupSize: Int = 3
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }
        
        val formatted = buildString {
            digits.reversed().forEachIndexed { index, c ->
                if (index > 0 && index % groupSize == 0) {
                    append(separator)
                }
                append(c)
            }
        }.reversed()
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset == 0) return 0
                if (offset == digits.length) return formatted.length
                
                var transformedOffset = offset
                var separatorCount = 0
                
                for (i in 0 until offset) {
                    if (i > 0 && i % groupSize == 0) {
                        separatorCount++
                    }
                }
                
                transformedOffset += separatorCount
                return transformedOffset
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset == 0) return 0
                if (offset == formatted.length) return digits.length
                
                var originalOffset = offset
                var separatorCount = 0
                
                for (i in 0 until offset) {
                    if (i > 0 && i % (groupSize + 1) == 0) {
                        separatorCount++
                    }
                }
                
                originalOffset -= separatorCount
                return originalOffset.coerceIn(0, digits.length)
            }
        }
        
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}