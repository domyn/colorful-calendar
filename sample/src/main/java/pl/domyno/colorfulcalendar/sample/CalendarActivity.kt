package pl.domyno.colorfulcalendar.sample

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.async_calendar_activity.*
import kotlinx.android.synthetic.main.calendar_activity.*
import pl.domyno.colorfulcalendar.AsyncCalendarView
import java.util.Calendar
import kotlin.random.Random
import kotlinx.coroutines.delay


class CalendarActivity : AppCompatActivity(), AsyncCalendarView.EventLoadHandler {
    override fun onLoadError(throwable: Throwable, startDate: Calendar, endDate: Calendar) {}

    override suspend fun loadEvents(startDate: Calendar, endDate: Calendar): Pair<Map<Calendar, List<Drawable>>, Map<Calendar, Int>> {
        val dates = (0..10).map { randomCalendar(startDate) }
        val icons = dates.map { it to random.nextInt(1, 6) }.toMap().mapValues { (0..it.value).map { randomDrawable } }
        val dayColors = dates.map { it to ContextCompat.getColor(applicationContext, R.color.red) }.toMap()
        return icons to dayColors
    }

    private val isAsync = true

    private val random = Random(Calendar.getInstance().timeInMillis)

    private val randomCalendar: Calendar
        get() = Calendar.getInstance().also { it.add(Calendar.DAY_OF_YEAR, random.nextInt(-60, 60)) }

    private val randomDrawable: Drawable
        get() = applicationContext.getDrawable(listOf(
                R.drawable.ic_mouse_24dp, R.drawable.ic_notifications_24dp,
                R.drawable.ic_palette_24dp, R.drawable.ic_schedule_24dp
        )[random.nextInt(4)])!!

    private suspend fun randomCalendar(startDate: Calendar): Calendar {
        delay(300L)
        return (startDate.clone() as Calendar).also { it.add(Calendar.DAY_OF_YEAR, random.nextInt(0, 30)) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("ConstantConditionIf")
        if (isAsync) {
            setContentView(R.layout.async_calendar_activity)
            asyncCalendarView.setEventLoadHandler(this)
        } else {
            setContentView(R.layout.calendar_activity)
            val dates = (0..16).map { randomCalendar }
            val icons = dates.map { it to random.nextInt(1, 6) }.toMap().mapValues { (0..it.value).map { randomDrawable } }
            calendarView.properties.update {
                dayColors = dates.map { it to ContextCompat.getColor(applicationContext, R.color.red) }.toMap()
                this.icons = icons
            }
        }
    }

}
