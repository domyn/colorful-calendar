package pl.domyno.colorfulcalendar

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import pl.domyno.colorfulcalendar.utils.resetToMidnight
import java.time.Month
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class CalendarProperties(val context: Context, typedArray: TypedArray, val calendarView: CalendarView) {

    var headerButtonTint: Int by Delegates.observable(
            typedArray.getColor(R.styleable.CalendarView_headerButtonTint, ContextCompat.getColor(context, android.R.color.black)), this::observeChanges)

    var headerTextColor: Int by Delegates.observable(
            typedArray.getColor(R.styleable.CalendarView_headerTextColor, ContextCompat.getColor(context, android.R.color.black)), this::observeChanges)

    var headerBackgroundColor: Int by Delegates.observable(
            typedArray.getColor(R.styleable.CalendarView_headerBackgroundColor, ContextCompat.getColor(context, android.R.color.holo_blue_dark)), this::observeChanges)

    var previousButtonSrc: Drawable by Delegates.observable(
            typedArray.getDrawable(R.styleable.CalendarView_previousButtonSrc)
                    ?: context.getDrawable(R.drawable.ic_keyboard_arrow_left_24dp)!!, this::observeChanges)

    var nextButtonSrc: Drawable by Delegates.observable(
            typedArray.getDrawable(R.styleable.CalendarView_nextButtonSrc)
                    ?: context.getDrawable(R.drawable.ic_keyboard_arrow_right_24dp)!!, this::observeChanges)

    var daysLabelsColor: Int by Delegates.observable(
            typedArray.getColor(R.styleable.CalendarView_daysLabelsColor, ContextCompat.getColor(context, android.R.color.black)), this::observeChanges)

    var todayLabelColor: Int by Delegates.observable(
            typedArray.getColor(R.styleable.CalendarView_todayLabelColor, ContextCompat.getColor(context, android.R.color.white)), this::observeChanges)

    var moreIconTint: Int by Delegates.observable(
            typedArray.getColor(R.styleable.CalendarView_moreIconTint, ContextCompat.getColor(context, android.R.color.black)), this::observeChanges)

    var backgroundColor: Int
            by Delegates.observable(typedArray.getColor(R.styleable.CalendarView_backgroundColor, ContextCompat.getColor(context, android.R.color.transparent)), this::observeChanges)

    var anotherMonthAlpha: Int = typedArray.getColor(R.styleable.CalendarView_anotherMonthAlpha, ContextCompat.getColor(context, R.color.alpha50)) or 0x00FFFFFF
        set(value) { field = value or 0x00FFFFFF; observeChanges() }

    var onPreviousPageChangeListener: CalendarView.OnPageChangeListener? = null
    var onNextPageChangeListener: CalendarView.OnPageChangeListener? = null
    var onDayClickListener: CalendarView.OnDayClickListener? = null

    internal var selectedDate: Calendar? = null
    internal var selectedView: View? = null

    private val firstDateCalendar = Calendar.getInstance().resetToMidnight()
    val initialDate: Calendar
        get() = firstDateCalendar.clone() as Calendar

    internal val minimumDate = initialDate.also { it.add(Calendar.MONTH, -(CALENDAR_SIZE / 2)) }
    internal val maximumDate = initialDate.also { it.add(Calendar.MONTH, CALENDAR_SIZE / 2) }

    var icons: Map<Calendar, List<Drawable>> = emptyMap()
        set(value) {
            field = value.mapKeys { (it.key.clone() as Calendar).resetToMidnight() }
            observeChanges()
        }

    var dayColors: Map<Calendar, Int> = emptyMap()
        set(value) {
            field = value.mapKeys { (it.key.clone() as Calendar).resetToMidnight() }
            observeChanges()
        }

    @Suppress("UNUSED_PARAMETER")
    private fun observeChanges(prop: KProperty<*>? = null, oldValue: Any? = null, newValue: Any? = null) {
        calendarView.refresh()
    }

    companion object {
        const val CALENDAR_SIZE = 23
    }
}
