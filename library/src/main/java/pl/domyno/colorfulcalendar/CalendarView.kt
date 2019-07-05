package pl.domyno.colorfulcalendar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.calendar_item.view.*
import kotlinx.android.synthetic.main.calendar_view.view.*
import pl.domyno.colorfulcalendar.CalendarProperties.Companion.CALENDAR_SIZE
import pl.domyno.colorfulcalendar.internal.PageAdapter
import pl.domyno.colorfulcalendar.utils.date
import pl.domyno.colorfulcalendar.utils.month
import pl.domyno.colorfulcalendar.utils.year
import java.util.*

class CalendarView : LinearLayout {
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

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    fun refresh() {
        applyStyleProperties()
        calendarMonthViewPager.adapter?.notifyDataSetChanged()
        setHeaderName(properties.initialDate)
        calendarMonthViewPager.currentItem = currentPage
    }

    private fun init(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView)
        properties = CalendarProperties(context, typedArray, this)
        typedArray.recycle()

        LayoutInflater.from(context).inflate(R.layout.calendar_view, this)

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
        calendarMonthViewPager.currentItem = currentPage
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

        // labels
        this.setBackgroundColor(properties.backgroundColor)
        listOf(labelMonday, labelTuesday, labelWednesday, labelThursday, labelFriday,
                labelSaturday, labelSunday).forEach { it.setTextColor(properties.daysLabelsColor) }
    }

    private fun onPreviousButtonClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        currentPage--
        calendarMonthViewPager.currentItem = currentPage
        properties.onPreviousPageChangeListener?.onChange()
        setHeaderName(properties.initialDate.also { it.add(Calendar.MONTH, currentPage - CALENDAR_SIZE / 2) })
    }

    private fun onNextButtonClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        currentPage++
        calendarMonthViewPager.currentItem = currentPage
        properties.onNextPageChangeListener?.onChange()
        setHeaderName(properties.initialDate.also { it.add(Calendar.MONTH, currentPage - CALENDAR_SIZE / 2) })
    }
}
