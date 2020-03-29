package com.zhimeng.battery.ui.reusable.views

import android.R.attr.dividerHeight
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class SpacesItemDecoration(private val horizontalSpace: Int,
                           private val verticalSpace: Int,
                           private val spanCount: Int,
                           private val dividerColor: Int = Color.TRANSPARENT) : RecyclerView.ItemDecoration() {

    var dividerPaint: Paint = Paint()

    init {
        dividerPaint.color = dividerColor
    }

    constructor(space: Int, spanCount: Int) : this(space, space, spanCount)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val childPosition = parent.getChildLayoutPosition(view)
        outRect.bottom = verticalSpace
        if (childPosition % spanCount == 0) {
            /* Left most */
            outRect.left = 0
            outRect.right = horizontalSpace / 2
        } else if ((childPosition - 1) % spanCount == 0) {
            /* Right most */
            outRect.right = 0
            outRect.left = horizontalSpace / 2
        } else {
            outRect.left = horizontalSpace / 2
            outRect.right = horizontalSpace / 2
        }

        if (parent.getChildLayoutPosition(view) < spanCount) {
            outRect.top = verticalSpace
        } else {
            outRect.top = 0
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount - 1
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        for (i in 0 until childCount - 1) {
            val view = parent.getChildAt(i)
            val top = view.bottom.toFloat()
            val bottom = (view.bottom + dividerHeight).toFloat()

            c.drawRect(left.toFloat(), top, right.toFloat(), bottom, dividerPaint)
        }
    }
}