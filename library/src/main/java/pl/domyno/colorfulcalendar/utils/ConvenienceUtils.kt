package pl.domyno.colorfulcalendar.utils

import java.lang.Exception

internal fun <T> suppressExceptions(block: () -> T?): T? {
    return try {
        block()
    } catch (_: Exception) {
        null
    }
}