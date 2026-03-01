@file:Suppress("DEPRECATION")

package nekit.corporation.utils

import java.text.NumberFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatWithLocale(value: Long, locale: Locale = Locale.ENGLISH): String {
    return NumberFormat.getIntegerInstance(locale).format(value)
}

fun formatWithLocale(value: Int, locale: Locale = Locale.ENGLISH): String {
    return NumberFormat.getIntegerInstance(locale).format(value)
}

fun formatDateDaysFromNow(
    daysToAdd: Long,
    locale: Locale = Locale("ru"),
    zone: ZoneId = ZoneId.systemDefault()
): String {
    val date = LocalDate.now(zone).plusDays(daysToAdd)
    val formatter = DateTimeFormatter.ofPattern("d MMMM", locale)
    return date.format(formatter)
}