package pl.domyno.colorfulcalendar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.calendar_view.view.*
import pl.domyno.colorfulcalendar.CalendarProperties.Companion.CALENDAR_SIZE
import pl.domyno.colorfulcalendar.internal.PageAdapter
import pl.domyno.colorfulcalendar.utils.month
import pl.domyno.colorfulcalendar.utils.year
import java.util.*

open class CalendarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {
    interface OnPageChangeListener {
        fun onChange()
    }

    interface OnDayClickListener {
        fun onDayClick(date: Calendar)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var properties: CalendarProperties
        private set

    private var currentPage = CALENDAR_SIZE / 2
    private lateinit var daysLabels: List<TextView>

    init {
        init(attrs)
    }

    fun refresh() {
        applyStyleProperties()
        calendarMonthViewPager.adapter?.notifyDataSetChanged()
        calendarMonthViewPager.currentItem = CALENDAR_SIZE / 2
        setHeaderName(properties.initialDate)
    }

    private fun init(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView)
        properties = CalendarProperties(context, typedArray, this)
        typedArray.recycle()

        LayoutInflater.from(context).inflate(R.layout.calendar_view, this)
        daysLabels = listOf(label1stDay, label2ndDay, label3rdDay, label4thDay, label5thDay, label6thDay, label7thDay)

        previousButton.setOnClickListener(this::onPreviousButtonClick)
        nextButton.setOnClickListener(this::onNextButtonClick)
        calendarMonthViewPager.adapter = PageAdapter(context, properties)
        calendarMonthViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                val month = properties.initialDate.also { it.add(Calendar.MONTH, position - CALENDAR_SIZE / 2) }
                when {
                    month < properties.minimumDate -> calendarMonthViewPager.currentItem = position + 1
                    month > properties.maximumDate -> calendarMonthViewPager.currentItem = position - 1
                    else -> {
                        setHeaderName(month)
                        setButtonLocks(month)
                        if (position < currentPage)
                            properties.onPreviousPageChangeListener?.onChange()
                        else
                            properties.onNextPageChangeListener?.onChange()

                        currentPage = position
                    }
                }
            }
        })

        applyStyleProperties()
        setHeaderName(properties.initialDate)
        setDayLabels()
        calendarMonthViewPager.currentItem = currentPage
    }

    private fun setDayLabels() {
        var dayOfTheWeek = properties.initialDate.firstDayOfWeek
        daysLabels.forEach {
            it.text = context.getString(when (dayOfTheWeek) {
                Calendar.MONDAY -> R.string.calendar_monday
                Calendar.TUESDAY -> R.string.calendar_tuesday
                Calendar.WEDNESDAY -> R.string.calendar_wednesday
                Calendar.THURSDAY -> R.string.calendar_thursday
                Calendar.FRIDAY -> R.string.calendar_friday
                Calendar.SATURDAY -> R.string.calendar_saturday
                else -> R.string.calendar_sunday
            })
            dayOfTheWeek = (dayOfTheWeek + 1).rem(7)
        }

    }

    private fun setButtonLocks(month: Calendar) {
        val previousMonth = (month.clone() as Calendar).also { it.add(Calendar.MONTH, -1) }
        val nextMonth = (month.clone() as Calendar).also { it.add(Calendar.MONTH, 1) }
        previousButton.isEnabled = previousMonth >= properties.minimumDate
        nextButton.isEnabled = nextMonth <= properties.maximumDate
    }

    @SuppressLint("SetTextI18n")
    private fun setHeaderName(calendar: Calendar) {
        currentDateLabel.text = context.resources.getStringArray(R.array.calendar_months_array)[calendar.month] + " " + calendar.year
    }

    private fun applyStyleProperties() {
        // header
        calendarHeader.setBackgroundColor(properties.headerBackgroundColor)
        previousButton.setImageDrawable(properties.previousButtonSrc)
        nextButton.setImageDrawable(properties.nextButtonSrc)
        listOf(previousButton, nextButton).forEach { it.setColorFilter(properties.headerButtonTint) }
        currentDateLabel.setTextColor(properties.headerTextColor)
        separator.setBackgroundColor(properties.separatorColor)

        // labels
        this.setBackgroundColor(properties.backgroundColor)
        daysLabels.forEach { it.setTextColor(properties.daysLabelsColor) }
    }

    private fun onPreviousButtonClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        val month = properties.initialDate.also { it.add(Calendar.MONTH, currentPage - 1 - CALENDAR_SIZE / 2) }
        setButtonLocks(month)
        currentPage--
        calendarMonthViewPager.currentItem = currentPage
        properties.onPreviousPageChangeListener?.onChange()
        setHeaderName(properties.initialDate.also { it.add(Calendar.MONTH, currentPage - CALENDAR_SIZE / 2) })
    }

    private fun onNextButtonClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        val month = properties.initialDate.also { it.add(Calendar.MONTH, currentPage + 1 - CALENDAR_SIZE / 2) }
        setButtonLocks(month)
        currentPage++
        calendarMonthViewPager.currentItem = currentPage
        properties.onNextPageChangeListener?.onChange()
        setHeaderName(properties.initialDate.also { it.add(Calendar.MONTH, currentPage - CALENDAR_SIZE / 2) })
    }
}
