package com.github.domyn.colorfulcalendar.utils

import java.util.*

val Calendar.dayOfMonth
    get() = this.get(Calendar.DAY_OF_MONTH)

val Calendar.month
    get() = this.get(Calendar.MONTH)

val Calendar.year
    get() = this.get(Calendar.YEAR)

/**
 * Number of days since epoch
 */
val Calendar.date
    get() = (this.clone() as Calendar).also { it.add(Calendar.YEAR, -1970) }.timeInMillis / 86400000

fun Calendar.resetToMidnight(): Calendar {
    this.set(Calendar.HOUR_OF_DAY, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.SECOND, 0)
    this.set(Calendar.MILLISECOND, 0)
    return this
}
