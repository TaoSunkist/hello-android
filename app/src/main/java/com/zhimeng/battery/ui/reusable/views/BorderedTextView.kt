package com.zhimeng.battery.ui.common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.zhimeng.battery.utilities.Dimens

class BorderedTextView : AppCompatTextView {

    private val borderPaint = Paint()
    private val borderRect = RectF()

    var borderColor: Int = Color.parseColor("#F1F1F1")
        set(value) {
            field = value
            borderPaint.color = value
            invalidate()
        }

    var borderWidth: Float = Dimens.dpToPx(1).toFloat()
        set(value) {
            field = value
            borderPaint.strokeWidth = value
            invalidate()
        }

    var drawBorder: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    var cornerRadius: Int = Dimens.marginMedium
        set(value) {
            field = value
            invalidate()
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
        setWillNotDraw(false)
        borderPaint.color = borderColor
        borderPaint.strokeWidth = borderWidth
        borderPaint.style = Paint.Style.STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        borderRect.set(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (drawBorder.not()) {
            return
        }
        canvas?.let {
            canvas.drawRoundRect(
                borderRect,
                cornerRadius.toFloat(),
                cornerRadius.toFloat(),
                borderPaint
            )
        }
    }
}