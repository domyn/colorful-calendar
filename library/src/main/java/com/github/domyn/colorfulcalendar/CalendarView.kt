package com.github.domyn.colorfulcalendar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.github.domyn.colorfulcalendar.CalendarProperties.Companion.CALENDAR_SIZE
import com.github.domyn.colorfulcalendar.databinding.CalendarViewBinding
import com.github.domyn.colorfulcalendar.internal.PageAdapter
import com.github.domyn.colorfulcalendar.utils.*
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

    private val binding = CalendarViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        init(attrs)
    }

    @JvmOverloads
    fun refresh(keepCurrentMonth: Boolean = false) {
        applyStyleProperties()
        binding.calendarMonthViewPager.adapter?.notifyDataSetChanged()
        if (!keepCurrentMonth) {
            binding.calendarMonthViewPager.currentItem = CALENDAR_SIZE / 2
            setHeaderName(properties.initialDate)
            properties.selectedView = null
            properties.selectedDate = null
        }
    }

    @Suppress("unused")
    @JvmOverloads
    fun selectDate(date: Calendar, sendEvent: Boolean = false) {
        var position = CALENDAR_SIZE / 2
        val month = properties.initialDate.resetToMidnight().also { it.set(Calendar.DAY_OF_MONTH, 1) }
        while (date < month) {
            month.add(Calendar.MONTH, -1)
            position--
        }
        val monthEnd = (month.clone() as Calendar).also { it.add(Calendar.MONTH, 1) }
        while (date >= monthEnd) {
            monthEnd.add(Calendar.MONTH, 1)
            position++
        }

        binding.calendarMonthViewPager.currentItem = position
        val view = binding.calendarMonthViewPager.findViewWithTag<View>(date.resetToMidnight()) ?: return

        properties.selectedView = view
        properties.selectedDate = date
        updateViewLabels(date, view, properties)
        if (sendEvent) properties.onDayClickListener?.onDayClick(date)
    }

    private fun init(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView)
        properties = CalendarProperties(context, typedArray, this)
        typedArray.recycle()

        daysLabels = binding.run {
            listOf(label1stDay, label2ndDay, label3rdDay, label4thDay, label5thDay, label6thDay, label7thDay)
        }

        binding.previousButton.setOnClickListener(this::onPreviousButtonClick)
        binding.nextButton.setOnClickListener(this::onNextButtonClick)
        binding.calendarMonthViewPager.adapter = PageAdapter(context, properties)
        binding.calendarMonthViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                val month = properties.initialDate.also { it.add(Calendar.MONTH, position - CALENDAR_SIZE / 2) }
                when {
                    month < properties.minimumDate -> binding.calendarMonthViewPager.currentItem = position + 1
                    month > properties.maximumDate -> binding.calendarMonthViewPager.currentItem = position - 1
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
                val lastDate = properties.selectedDate
                val lastView = properties.selectedView
                if (lastView != null && lastDate != null && lastDate.date != properties.initialDate.date) {
                    suppressExceptions { setDayViewLabel(lastDate, lastView, properties, lastDate.month) }
                    properties.selectedDate = null
                    properties.selectedView = null
                }
            }
        })

        applyStyleProperties()
        setHeaderName(properties.initialDate)
        setDayLabels()
        binding.calendarMonthViewPager.currentItem = currentPage
    }

    private fun setDayLabels() {
        var dayOfTheWeek = properties.initialDate.firstDayOfWeek - 1
        daysLabels.forEach {
            it.text = context.getString(when (dayOfTheWeek + 1) {
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
        binding.previousButton.isEnabled = previousMonth >= properties.minimumDate
        binding.nextButton.isEnabled = nextMonth <= properties.maximumDate
    }

    @SuppressLint("SetTextI18n")
    private fun setHeaderName(calendar: Calendar) {
        binding.currentDateLabel.text = context.resources.getStringArray(R.array.calendar_months_array)[calendar.month] + " " + calendar.year
    }

    private fun applyStyleProperties() {
        // header
        binding.calendarHeader.setBackgroundColor(properties.headerBackgroundColor)
        binding.previousButton.setImageDrawable(properties.previousButtonSrc)
        binding.nextButton.setImageDrawable(properties.nextButtonSrc)
        listOf(binding.previousButton, binding.nextButton).forEach { it.setColorFilter(properties.headerButtonTint) }
        binding.currentDateLabel.setTextColor(properties.headerTextColor)
        binding.separator.setBackgroundColor(properties.separatorColor)

        // labels
        this.setBackgroundColor(properties.backgroundColor)
        daysLabels.forEach { it.setTextColor(properties.daysLabelsColor) }
    }

    private fun onPreviousButtonClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        val month = properties.initialDate.also { it.add(Calendar.MONTH, currentPage - 1 - CALENDAR_SIZE / 2) }
        setButtonLocks(month)
        currentPage--
        binding.calendarMonthViewPager.currentItem = currentPage
        properties.onPreviousPageChangeListener?.onChange()
        setHeaderName(properties.initialDate.also { it.add(Calendar.MONTH, currentPage - CALENDAR_SIZE / 2) })
    }

    private fun onNextButtonClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        val month = properties.initialDate.also { it.add(Calendar.MONTH, currentPage + 1 - CALENDAR_SIZE / 2) }
        setButtonLocks(month)
        currentPage++
        binding.calendarMonthViewPager.currentItem = currentPage
        properties.onNextPageChangeListener?.onChange()
        setHeaderName(properties.initialDate.also { it.add(Calendar.MONTH, currentPage - CALENDAR_SIZE / 2) })
    }
}
