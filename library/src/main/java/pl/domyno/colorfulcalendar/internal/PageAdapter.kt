package pl.domyno.colorfulcalendar.internal

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import androidx.viewpager.widget.PagerAdapter
import pl.domyno.colorfulcalendar.CalendarProperties
import pl.domyno.colorfulcalendar.CalendarProperties.Companion.CALENDAR_SIZE
import pl.domyno.colorfulcalendar.R
import pl.domyno.colorfulcalendar.utils.*
import java.util.*

class PageAdapter(val context: Context, val properties: CalendarProperties) : PagerAdapter() {
    private var gridView: GridView? = null

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    override fun getCount() = CALENDAR_SIZE

    override fun getItemPosition(`object`: Any) = POSITION_NONE

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        gridView = LayoutInflater.from(context).inflate(R.layout.calendar_month_grid, null) as GridView
        loadMonthView(position)
        gridView?.setOnItemClickListener(this::onItemClick)
        container.addView(gridView)
        return gridView!!
    }

    private fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val calendar = properties.initialDate.also { it.time = parent.getItemAtPosition(position) as Date }
        val currentMonth = (parent.adapter as DayAdapter).month
        if (calendar.month != currentMonth)
            return

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

    private fun loadMonthView(position: Int) {
        val calendar = properties.initialDate.also {
            it.add(Calendar.MONTH, position - CALENDAR_SIZE / 2)
            it.set(Calendar.DAY_OF_MONTH, 1)
            val dayOfWeek = it.get(Calendar.DAY_OF_WEEK)
            val firstCell = (if (dayOfWeek < it.firstDayOfWeek) 7 else 0) + dayOfWeek - it.firstDayOfWeek
            it.add(Calendar.DAY_OF_MONTH, -firstCell)
        }

        // 42 cells on page
        val days = (0..41).map { i ->
            (calendar.clone() as Calendar).also {
                it.add(Calendar.DAY_OF_MONTH, i)
            }.time!!
        }

        gridView?.adapter = DayAdapter(context, properties, days,
                properties.initialDate.also { it.add(Calendar.MONTH, position - CALENDAR_SIZE / 2) }.get(Calendar.MONTH))
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}