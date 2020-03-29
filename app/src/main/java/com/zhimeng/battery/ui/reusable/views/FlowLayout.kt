package com.zhimeng.battery.ui.reusable.views

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.zhimeng.battery.R
import java.util.*

class FlowLayout : ViewGroup {

    private var mVerticalAlignType: VerticalAlignType? = null

    private var mHorizontalAlignType: HorizontalAlignType? = null

    private var mHorizontalSpacing: Int = 0

    private var mVerticalSpacing: Int = 0

    private var mMaximumWidth: Int = 0

    enum class VerticalAlignType {
        TOP,
        BOTTOM,
        CENTER
    }

    enum class HorizontalAlignType {
        LEFT,
        CENTER
    }

    constructor(context: Context) : super(context) {
        commonInit(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        commonInit(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        commonInit(context, attrs)
    }

    private fun commonInit(context: Context, attrs: AttributeSet?) {
        mVerticalAlignType = VerticalAlignType.CENTER
        mHorizontalAlignType = HorizontalAlignType.LEFT
        mHorizontalSpacing = 24
        mVerticalSpacing = 24
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout)
            mHorizontalSpacing = a.getDimension(
                R.styleable.FlowLayout_oh_flow_layout_horizontal_margin,
                mHorizontalSpacing.toFloat()
            ).toInt()
            mVerticalSpacing = a.getDimension(
                R.styleable.FlowLayout_oh_flow_layout_vertical_margin,
                mVerticalSpacing.toFloat()
            ).toInt()
            val type = a.getInt(R.styleable.FlowLayout_oh_flow_layout_vertical_align_type, 0)
            when (type) {
                0 -> mVerticalAlignType = VerticalAlignType.CENTER
                1 -> mVerticalAlignType = VerticalAlignType.BOTTOM
                else -> mVerticalAlignType = VerticalAlignType.TOP
            }
            val hType = a.getInt(R.styleable.FlowLayout_oh_flow_layout_horizontal_align_type, 0)
            when (hType) {
                1 -> mHorizontalAlignType = HorizontalAlignType.CENTER
                else -> mHorizontalAlignType = HorizontalAlignType.LEFT
            }
            a.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            val v = getChildAt(i)
            measureChild(v, widthMeasureSpec, heightMeasureSpec)
        }
        mMaximumWidth = MeasureSpec.getSize(widthMeasureSpec)
        calculateLayout()
    }

    private fun calculateLayout() {
        val childCount = childCount
        var currentLineX = paddingLeft.toFloat()
        var currentLineY = paddingTop.toFloat()
        var currentLineMaxHeight = 0f
        val currentLineChildren = ArrayList<View>()

        var i = 0
        while (i < childCount) {
            val child = getChildAt(i)

            /* Ignore gone child */
            if (child.visibility == View.GONE) {
                i++
                continue
            }
            val childWidth = child.measuredWidth.toFloat()
            val childHeight = child.measuredHeight.toFloat()
            if (currentLineX + mHorizontalSpacing.toFloat() + childWidth < mMaximumWidth - paddingRight || currentLineChildren.size == 0) {
                /* still in the same line */
                currentLineMaxHeight = Math.max(currentLineMaxHeight, childHeight)
                currentLineChildren.add(child)
                currentLineX += childWidth + mHorizontalSpacing
                i++
            } else {
                /* assign rect for each child */
                assignLayoutForLine(currentLineChildren, currentLineY, currentLineMaxHeight)
                /* should go into a new line */
                currentLineChildren.clear()
                currentLineY += currentLineMaxHeight + mVerticalSpacing
                currentLineMaxHeight = 0f
                currentLineX = paddingLeft.toFloat()
            }
        }

        if (currentLineChildren.size > 0) {
            /* one more line */
            assignLayoutForLine(currentLineChildren, currentLineY, currentLineMaxHeight)
            currentLineChildren.clear()
            currentLineY += currentLineMaxHeight
        } else {
            currentLineY -= mVerticalSpacing.toFloat()
        }
        setMeasuredDimension(mMaximumWidth, currentLineY.toInt() + paddingBottom)
    }

    private fun assignLayoutForLine(children: List<View>, yStart: Float, maxHeight: Float) {
        var xStart = paddingLeft.toFloat()
        if (mHorizontalAlignType == HorizontalAlignType.CENTER) {
            var widthNeeded = 0f
            for (i in children.indices) {
                val child = children[i]
                val childWidth = child.measuredWidth.toFloat()
                widthNeeded += childWidth + mHorizontalSpacing
            }
            xStart = (mMaximumWidth - (widthNeeded - mHorizontalSpacing)) / 2
        }


        for (i in children.indices) {
            val child = children[i]
            val childWidth = child.measuredWidth.toFloat()
            val childHeight = child.measuredHeight.toFloat()
            when (mVerticalAlignType) {
                VerticalAlignType.CENTER -> child.setTag(
                    R.id.view_layout_info_tag_key,
                    RectF(
                        xStart,
                        yStart + (maxHeight - childHeight) / 2,
                        xStart + childWidth,
                        yStart + (maxHeight - childHeight) / 2 + childHeight
                    )
                )
                VerticalAlignType.BOTTOM -> child.setTag(
                    R.id.view_layout_info_tag_key,
                    RectF(
                        xStart,
                        yStart + maxHeight - childHeight,
                        xStart + childWidth,
                        yStart + maxHeight
                    )
                )
                VerticalAlignType.TOP -> child.setTag(
                    R.id.view_layout_info_tag_key,
                    RectF(
                        xStart,
                        yStart,
                        xStart + childWidth,
                        yStart + childHeight
                    )
                )
            }
            xStart += childWidth + mHorizontalSpacing
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val rect = child.getTag(R.id.view_layout_info_tag_key) as RectF
            if (rect != null) {
                child.layout(
                    rect.left.toInt(),
                    rect.top.toInt(),
                    rect.right.toInt(),
                    rect.bottom.toInt()
                )
            }
        }
    }
}
