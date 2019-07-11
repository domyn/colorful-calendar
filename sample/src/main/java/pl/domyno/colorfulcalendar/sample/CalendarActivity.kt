package pl.domyno.colorfulcalendar.sample

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.calendar_activity.*
import java.util.Calendar
import kotlin.random.Random


class CalendarActivity : AppCompatActivity() {

    private val random = Random(Calendar.getInstance().timeInMillis)

    private val randomCalendar: Calendar
        get() = Calendar.getInstance().also { it.add(Calendar.DAY_OF_YEAR, random.nextInt(-60, 60)) }

    private val randomDrawable: Drawable
        get() = applicationContext.getDrawable(listOf(
                R.drawable.ic_mouse_24dp, R.drawable.ic_notifications_24dp,
                R.drawable.ic_palette_24dp, R.drawable.ic_schedule_24dp
        )[random.nextInt(4)])!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_activity)

        val dates = (0..16).map { randomCalendar }
        val icons = dates.map { it to random.nextInt(1, 6) }.toMap().mapValues { (0..it.value).map { randomDrawable } }
        calendarView.properties.daysLabelsColor = ContextCompat.getColor(applicationContext, android.R.color.holo_purple)
        calendarView.properties.dayColors = dates.map { it to ContextCompat.getColor(applicationContext, R.color.red) }.toMap()
        calendarView.properties.icons = icons
    }

}
