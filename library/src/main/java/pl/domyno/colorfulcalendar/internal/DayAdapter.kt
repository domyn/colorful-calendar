package pl.domyno.colorfulcalendar.internal

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import pl.domyno.colorfulcalendar.CalendarProperties
import pl.domyno.colorfulcalendar.R
import pl.domyno.colorfulcalendar.utils.date
import pl.domyno.colorfulcalendar.utils.dayOfMonth
import pl.domyno.colorfulcalendar.utils.setDayViewLabel
import pl.domyno.colorfulcalendar.utils.setTodayViewLabel
import java.util.*


class DayAdapter(context: Context,
                 private val properties: CalendarProperties,
                 days: List<Date>,
                 month: Int) : ArrayAdapter<Date>(context, R.layout.calendar_item, days) {

    val month = (month + 12).rem(12)
    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.calendar_item, parent, false)

        val dateLabel = view.findViewById<TextView>(R.id.dateLabel)
        val iconMore = view.findViewById<ImageView>(R.id.iconMore)
        val iconViews = listOf(R.id.icon1, R.id.icon2, R.id.icon3, R.id.icon4).map { view.findViewById<ImageView>(it) }

        val day = properties.initialDate.also { it.time = getItem(position) }

        setIcons(day, iconViews, iconMore)

        if (day.date == properties.initialDate.date)
            setTodayViewLabel(day, view, properties)
        else
            setDayViewLabel(day, view, properties, month)

        dateLabel.text = day.dayOfMonth.toString()

        return view
    }


    private fun setIcons(day: Calendar, iconViews: List<ImageView>, iconMore: ImageView) {
        val icons = properties.icons[day].orEmpty()
        for (i in 0..3) {
            if (icons.size <= i)
                iconViews[i].visibility = View.GONE
            else
                iconViews[i].setImageDrawable(icons[i])
        }

        if (icons.size == 3) {
            iconViews[3].visibility = View.INVISIBLE
            iconMore.visibility = View.INVISIBLE
        }

        if (icons.size > 4) {
            iconViews[3].visibility = View.INVISIBLE
            iconMore.visibility = View.VISIBLE
            iconMore.setColorFilter(properties.moreIconTint, PorterDuff.Mode.MULTIPLY)
        }
    }


}