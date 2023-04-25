package com.github.domyn.colorfulcalendar.utils

import android.view.View
import com.github.domyn.colorfulcalendar.CalendarProperties
import java.lang.Exception
import java.util.*

internal fun <T> suppressExceptions(block: () -> T?): T? {
    return try {
        block()
    } catch (_: Exception) {
        null
    }
}

internal fun updateViewLabels(calendar: Calendar, view: View, properties: CalendarProperties) {
    val lastDate = properties.selectedDate
    val lastView = properties.selectedView

    properties.onDayClickListener?.onDayClick(calendar)
    if (lastView != null && lastDate != null && lastDate.date != properties.initialDate.date)
        setDayViewLabel(lastDate, lastView, properties, lastDate.month)

    properties.selectedDate = calendar
    properties.selectedView = view

    if (properties.initialDate.date != calendar.date)
        setSelectedDayViewLabel(calendar, view, properties)
}
