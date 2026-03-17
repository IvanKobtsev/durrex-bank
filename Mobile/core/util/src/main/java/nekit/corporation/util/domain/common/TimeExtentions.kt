package nekit.corporation.util.domain.common

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun LocalDate.toShortFormat(locale: Locale): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", locale)
    return this.format(formatter)
}

fun LocalDate.toLongFormat(locale: Locale): String {
    val formatter = DateTimeFormatter.ofPattern("d MMMM, EEE", locale)
    return this.format(formatter)
}

fun Instant.toDate(): String {
    val zone = ZoneId.systemDefault()
    val zonedDateTime = atZone(zone)
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    return zonedDateTime.format(formatter)
}