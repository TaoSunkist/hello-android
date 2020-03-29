package com.zhimeng.battery.ui.reusable.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.ImageButton

class ImageButtonWithDot @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : ImageButton(context, attrs, defStyle, defStyleRes) {

    var showDot: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    var paint = Paint()

    init {
        setWillNotDraw(false)
        paint.color = Color.RED
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (showDot && canvas != null) {
            val width = canvas.width
            val height = canvas.height
            val dotLength = width * 0.2f
            val dotX = 0.55f
            val dotY = 0.25f

            canvas.drawOval(
                RectF(
                    width * dotX,
                    height * dotY,
                    width * dotX + dotLength,
                    height * dotY + dotLength
                ), paint
            )
        }
    }
}