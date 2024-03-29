package com.github.domyn.colorfulcalendar.utils

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.github.domyn.colorfulcalendar.CalendarProperties
import com.github.domyn.colorfulcalendar.R
import java.util.*

fun setDayViewLabel(day: Calendar, view: View, properties: CalendarProperties, currentMonth: Int) {
    val dateLabel = view.findViewById<TextView>(R.id.dateLabel)
    setLabelColor(day, dateLabel, properties, currentMonth)
    dateLabel.setBackgroundColor(ContextCompat.getColor(properties.context, android.R.color.transparent))
}

fun setSelectedDayViewLabel(day: Calendar, view: View, properties: CalendarProperties) {
    val color = calculateLabelColor(day, properties, day.month)
    val dateLabel = view.findViewById<TextView>(R.id.dateLabel)
    dateLabel.background = AppCompatResources.getDrawable(properties.context, R.drawable.background_circle_selected)
            ?.also { it.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY) }

}

fun setTodayViewLabel(day: Calendar, view: View, properties: CalendarProperties) {
    val color = calculateLabelColor(day, properties, day.month)
    val dateLabel = view.findViewById<TextView>(R.id.dateLabel)
    dateLabel.background = AppCompatResources.getDrawable(properties.context, R.drawable.background_circle_today)
            ?.also { it.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY) }
    dateLabel.setTextColor(properties.todayLabelColor)
}

@ColorInt
private fun calculateLabelColor(day: Calendar, properties: CalendarProperties, currentMonth: Int): Int {
    var color = properties.dayColors[day] ?: properties.daysLabelsColor
    if (!isCurrentMonth(day, currentMonth)) {
        color = color and properties.anotherMonthAlpha
    }
    return color
}

private fun setLabelColor(day: Calendar, dateLabel: TextView, properties: CalendarProperties, currentMonth: Int) {
    dateLabel.setTextColor(calculateLabelColor(day, properties, currentMonth))
}

private fun isCurrentMonth(day: Calendar, currentMonth: Int): Boolean {
    return day.month == currentMonth
}
