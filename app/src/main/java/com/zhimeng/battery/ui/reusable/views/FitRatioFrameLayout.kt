package com.zhimeng.battery.ui.reusable.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhimeng.battery.R

class FitRatioFrameLayout : FrameLayout {

    private var mWidthWeight = 1
    private var mHeightWeight = 1
    private var mFitTo = FIT_NONE

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

    private fun commonInit(
        context: Context,
        attrs: AttributeSet?
    ) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.FitRatioFrameLayout)
            mWidthWeight = a.getInt(R.styleable.FitRatioFrameLayout_width_weight, mWidthWeight)
            mHeightWeight = a.getInt(R.styleable.FitRatioFrameLayout_height_weight, mHeightWeight)
            mFitTo = a.getInt(R.styleable.FitRatioFrameLayout_fit_to, mFitTo)
            a.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val measuredHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (mFitTo == FIT_WIDTH) {
            val height = 1.0f * measuredWidth / mWidthWeight * mHeightWeight
            super.onMeasure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(height.toInt(), MeasureSpec.EXACTLY)
            )
//            setMeasuredDimension(measuredWidth, height.toInt())
        } else if (mFitTo == FIT_HEIGHT) {
            val width = 1.0f * measuredHeight / mHeightWeight * mWidthWeight
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(width.toInt(), MeasureSpec.EXACTLY),
                heightMeasureSpec
            )
//            setMeasuredDimension(width.toInt(), measuredHeight)
        }

//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun setWeight(widthWeight: Int, heightWeight: Int) {
        mWidthWeight = widthWeight
        mHeightWeight = heightWeight
    }

    companion object {
        const val FIT_NONE = 2
        const val FIT_WIDTH = 0
        const val FIT_HEIGHT = 1
    }
}
