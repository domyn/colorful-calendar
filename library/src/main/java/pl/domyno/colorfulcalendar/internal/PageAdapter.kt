package pl.domyno.colorfulcalendar.internal

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ProgressBar
import androidx.viewpager.widget.PagerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.domyno.colorfulcalendar.CalendarProperties
import pl.domyno.colorfulcalendar.CalendarProperties.Companion.CALENDAR_SIZE
import pl.domyno.colorfulcalendar.R
import pl.domyno.colorfulcalendar.utils.*
import java.util.*

class PageAdapter(private val context: Context, private val properties: CalendarProperties) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    override fun getCount() = CALENDAR_SIZE

    override fun getItemPosition(`object`: Any) = POSITION_NONE

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val page = LayoutInflater.from(context).inflate(R.layout.calendar_month_grid, null)
        val gridView = page.findViewById(R.id.gridView) as GridView
        val progressBar = page.findViewById<View>(R.id.progressBar)
        loadMonthView(gridView, position)
        gridView.setOnItemClickListener(this::onItemClick)
        container.addView(page)

        if (properties.isAsync) {
            gridView.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            GlobalScope.launch(Dispatchers.Main) {
                asyncLoadMonthView(gridView, progressBar, position)
            }
        }

        return page
    }

    private fun onItemClick(parent: AdapterView<*>, view: View, position: Int, @Suppress("UNUSED_PARAMETER") id: Long) {
        val calendar = properties.initialDate.also { it.time = parent.getItemAtPosition(position) as Date }
        val currentMonth = (parent.adapter as DayAdapter).month
        if (calendar.month != currentMonth)
            return

        updateViewLabels(calendar, view, properties)
    }



    private fun loadMonthView(gridView: GridView, position: Int) {
        val thisMonth = properties.initialDate.also { it.add(Calendar.MONTH, position - CALENDAR_SIZE / 2) }
        val calendar = (thisMonth.clone() as Calendar).also {
            it.set(Calendar.DAY_OF_MONTH, 1)
            val dayOfWeek = it.get(Calendar.DAY_OF_WEEK)
            val firstCell = (if (dayOfWeek < it.firstDayOfWeek) 7 else 0) + dayOfWeek - it.firstDayOfWeek
            it.add(Calendar.DAY_OF_MONTH, -firstCell)
        }

        // calculate cells on page
        val cells = if ((calendar.clone() as Calendar).also { it.add(Calendar.DAY_OF_MONTH, 35) }.month != thisMonth.month) 35 else 42

        val days = (0 until cells).map { i ->
            (calendar.clone() as Calendar).also {
                it.add(Calendar.DAY_OF_MONTH, i)
            }.time!!
        }

        gridView.adapter = DayAdapter(context, properties, days, thisMonth.month)
        gridView.tag = position
    }

    private suspend fun asyncLoadMonthView(gridView: GridView, progressBar: View, position: Int) {
        val thisMonth = properties.initialDate.also {
            it.add(Calendar.MONTH, position - CALENDAR_SIZE / 2)
            it.set(Calendar.DAY_OF_MONTH, 1)
            it.resetToMidnight()
        }
        val nextMonth = (thisMonth.clone() as Calendar).also { it.add(Calendar.MONTH, 1) }
        val previousMonth = (thisMonth.clone() as Calendar).also { it.add(Calendar.MONTH, -1) }

        try {
            val deferreds = listOf(previousMonth, thisMonth, nextMonth)
                    .filter { it !in properties.loadedPages }
                    .map { it to io { properties.eventLoadHandler?.loadEvents(thisMonth, nextMonth) } }.toMap()

            deferreds.mapValues { it.value.await() }.filterValues { it != null }.forEach {
                properties.icons = properties.icons.toMutableMap().apply { putAll(it.value!!.first) }
                properties.dayColors = properties.dayColors.toMutableMap().apply { putAll(it.value!!.second) }
                properties.loadedPages.add(it.key)
            }
        } catch (e: Exception) {
            properties.eventLoadHandler!!.onLoadError(e, thisMonth, nextMonth)
        }

        loadMonthView(gridView, position)
        progressBar.visibility = View.GONE
        gridView.visibility = View.VISIBLE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}