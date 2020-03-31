package pl.domyno.colorfulcalendar

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import pl.domyno.colorfulcalendar.utils.resetToMidnight
import java.util.*
import kotlin.collections.ArrayList

class CalendarProperties(val context: Context, typedArray: TypedArray, private val calendarView: CalendarView) {

    var headerButtonTint: Int =
            typedArray.getColor(R.styleable.CalendarView_headerButtonTint, ContextCompat.getColor(context, android.R.color.black))

    var headerTextColor: Int =
            typedArray.getColor(R.styleable.CalendarView_headerTextColor, ContextCompat.getColor(context, android.R.color.black))

    var headerBackgroundColor: Int =
            typedArray.getColor(R.styleable.CalendarView_headerBackgroundColor, ContextCompat.getColor(context, android.R.color.holo_blue_dark))

    var previousButtonSrc: Drawable =
            typedArray.getDrawable(R.styleable.CalendarView_previousButtonSrc)
                    ?: context.getDrawable(R.drawable.ic_keyboard_arrow_left_24dp)!!

    var nextButtonSrc: Drawable =
            typedArray.getDrawable(R.styleable.CalendarView_nextButtonSrc)
                    ?: context.getDrawable(R.drawable.ic_keyboard_arrow_right_24dp)!!

    var daysLabelsColor: Int =
            typedArray.getColor(R.styleable.CalendarView_daysLabelsColor, ContextCompat.getColor(context, android.R.color.black))

    var todayLabelColor: Int =
            typedArray.getColor(R.styleable.CalendarView_todayLabelColor, ContextCompat.getColor(context, android.R.color.white))

    var moreIconTint: Int =
            typedArray.getColor(R.styleable.CalendarView_moreIconTint, ContextCompat.getColor(context, android.R.color.black))

    var backgroundColor: Int =
            typedArray.getColor(R.styleable.CalendarView_backgroundColor, ContextCompat.getColor(context, android.R.color.transparent))

    var separatorColor: Int =
            typedArray.getColor(R.styleable.CalendarView_separatorColor, ContextCompat.getColor(context, android.R.color.black))

    var anotherMonthAlpha: Int = typedArray.getColor(R.styleable.CalendarView_anotherMonthAlpha, ContextCompat.getColor(context, R.color.alpha50)) or 0x00FFFFFF
        set(value) {
            field = value or 0x00FFFFFF
            iconAlpha = Color.alpha(field) / 256f
        }

    var onPreviousPageChangeListener: CalendarView.OnPageChangeListener? = null
    var onNextPageChangeListener: CalendarView.OnPageChangeListener? = null
    var onDayClickListener: CalendarView.OnDayClickListener? = null

    var selectedDate: Calendar? = null
        internal set
    internal var selectedView: View? = null

    // async mode
    internal var isAsync: Boolean = false
    internal var eventLoadHandler: AsyncCalendarView.EventLoadHandler? = null
        set(value) {
            require(value != null || field == null) { "handler cannot be set to null" }
            field = value
        }

    internal val loadedPages: MutableList<Calendar> = ArrayList()

    private val firstDateCalendar = Calendar.getInstance().resetToMidnight()
    val initialDate: Calendar
        get() = firstDateCalendar.clone() as Calendar

    internal var iconAlpha: Float = Color.alpha(anotherMonthAlpha) / 256f
        private set

    internal val minimumDate = initialDate.also { it.add(Calendar.MONTH, -(CALENDAR_SIZE / 2)) }
    internal val maximumDate = initialDate.also { it.add(Calendar.MONTH, CALENDAR_SIZE / 2) }

    var icons: Map<Calendar, List<Drawable>> = emptyMap()
        set(value) { field = value.mapKeys { (it.key.clone() as Calendar).resetToMidnight() } }

    var dayColors: Map<Calendar, Int> = emptyMap()
        set(value) { field = value.mapKeys { (it.key.clone() as Calendar).resetToMidnight() } }


    fun update(cmd: CalendarProperties.() -> Unit) {
        this.cmd()
        calendarView.refresh()
    }

    companion object {
        const val CALENDAR_SIZE = 23
    }
}
