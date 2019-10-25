package pl.domyno.colorfulcalendar

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import java.util.*

class AsyncCalendarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : CalendarView(context, attrs, defStyle) {
    interface EventLoadHandler {
        suspend fun loadEvents(startDate: Calendar, endDate: Calendar):
                Pair<Map<Calendar, List<Drawable>>, Map<Calendar, Int>>

        fun onLoadError(throwable: Throwable, startDate: Calendar, endDate: Calendar)
    }

    init {
        properties.isAsync = true
    }

    fun setEventLoadHandler(eventLoadHandler: EventLoadHandler) {
        properties.eventLoadHandler = eventLoadHandler
    }
}