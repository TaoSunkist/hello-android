package com.zhimeng.battery.ui.reusable.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.zhimeng.battery.utilities.makeAtMostMeasure
import com.zhimeng.battery.utilities.makeExactlyMeasure
import com.zhimeng.battery.utilities.setFrame

class EqualWeightLayout : ViewGroup {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    var spacing: Int = 0

    init {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (childCount == 0) {
            setMeasuredDimension(0, 0)
            return
        }
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val childWidth =
            (width - (childCount - 1) * spacing - paddingStart - paddingEnd) / childCount
        var maxHeight = 0
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            measureChild(
                child,
                makeExactlyMeasure(childWidth),
                makeAtMostMeasure(height - paddingTop - paddingBottom)
            )
            maxHeight = Math.max(child.measuredHeight, maxHeight)
        }
        setMeasuredDimension(width, maxHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount == 0) {
            return
        }
        val width = r - l
        val height = b - t
        val childWidth =
            (width - paddingStart - paddingEnd - (childCount - 1) * spacing) / childCount
        var currX = paddingStart
        val currY = paddingTop
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            child.setFrame(
                currX,
                (height - child.measuredHeight) / 2,
                childWidth,
                child.measuredHeight
            )
            currX += spacing + childWidth
        }
    }
}