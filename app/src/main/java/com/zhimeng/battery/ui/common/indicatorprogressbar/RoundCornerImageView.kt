package com.zhimeng.battery.ui.common.indicatorprogressbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.zhimeng.battery.utilities.Dimens.dpToPx

class RoundCornerImageView : AppCompatImageView {
    private var mRadius = 18f
    private val mClipPath = Path()
    private val mRect = RectF()

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {}

    init {
    }

    fun setRadiusDp(dp: Int) {
        mRadius = dpToPx(dp).toFloat()
        postInvalidate()
    }

    fun setRadiusPx(px: Int) {
        mRadius = px.toFloat()
        postInvalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        mRect[0f, 0f, this.width.toFloat()] = this.height.toFloat()
        mClipPath.reset() // remember to reset path
        mClipPath.addRoundRect(mRect, mRadius, mRadius, Path.Direction.CW)
        canvas.clipPath(mClipPath)

        super.onDraw(canvas)
    }
}