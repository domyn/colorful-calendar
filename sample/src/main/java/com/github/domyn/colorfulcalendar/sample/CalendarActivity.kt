package com.github.domyn.colorfulcalendar.sample

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.github.domyn.colorfulcalendar.AsyncCalendarView
import com.github.domyn.colorfulcalendar.CalendarView
import java.util.Calendar
import kotlin.random.Random
import kotlinx.coroutines.delay


class CalendarActivity : AppCompatActivity(), AsyncCalendarView.EventLoadHandler {
    override fun onLoadError(throwable: Throwable, startDate: Calendar, endDate: Calendar) {}

    override suspend fun loadEvents(startDate: Calendar, endDate: Calendar): Pair<Map<Calendar, List<Drawable>>, Map<Calendar, Int>> {
        val dates = (0..10).map { randomCalendar(startDate) }
        val icons = dates.associateWith { random.nextInt(1, 6) }.mapValues { (0..it.value).map { randomDrawable } }
        val dayColors = dates.associateWith { ContextCompat.getColor(applicationContext, R.color.red) }
        return icons to dayColors
    }

    private lateinit var calendarView: CalendarView

    private val isAsync = true

    private val random = Random(Calendar.getInstance().timeInMillis)

    private val randomCalendar: Calendar
        get() = Calendar.getInstance().also { it.add(Calendar.DAY_OF_YEAR, random.nextInt(-60, 60)) }

    private val randomDrawable: Drawable
        get() = AppCompatResources.getDrawable(this, listOf(
                R.drawable.ic_mouse_24dp, R.drawable.ic_notifications_24dp,
                R.drawable.ic_palette_24dp, R.drawable.ic_schedule_24dp
        )[random.nextInt(4)])!!

    private suspend fun randomCalendar(startDate: Calendar): Calendar {
        delay(300L)
        return (startDate.clone() as Calendar).also { it.add(Calendar.DAY_OF_YEAR, random.nextInt(0, 30)) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isAsync) {
            setContentView(R.layout.async_calendar_activity)
            calendarView = findViewById(R.id.asyncCalendarView)
            (calendarView as AsyncCalendarView).setEventLoadHandler(this)
        } else {
            setContentView(R.layout.calendar_activity)
            calendarView = findViewById(R.id.calendarView)
            val dates = (0..16).map { randomCalendar }
            val icons = dates.associateWith { random.nextInt(1, 6) }.mapValues { (0..it.value).map { randomDrawable } }
            calendarView.properties.update {
                dayColors = dates.associateWith { ContextCompat.getColor(applicationContext, R.color.red) }
                this.icons = icons
            }
        }
    }

}
