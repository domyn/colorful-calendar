package pl.domyno.colorfulcalendar.views

import android.content.Context
import android.util.AttributeSet
import android.widget.GridView
import androidx.viewpager.widget.ViewPager

class WrappingViewPager @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val currentChild = findViewWithTag<GridView>(currentItem)
        val height = currentChild
                    ?.also { v -> v.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)) }
                    ?.measuredHeight ?: 0
        val heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)

        super.onMeasure(widthMeasureSpec, heightSpec)
    }
}