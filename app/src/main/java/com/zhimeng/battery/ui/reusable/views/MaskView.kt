package com.zhimeng.battery.ui.reusable.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class MaskView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyle, defStyleRes) {

    private var diameterRatio = 0.65f
    private var maskColor = Color.parseColor("#30000000")
    val paint = Paint()
    var path: Path? = null

    init {
        setWillNotDraw(false)
        paint.apply {
            color = maskColor
            style = Paint.Style.FILL
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        path?.let {
            canvas!!.drawPath(it, paint)
        } ?: run {
            val width = canvas!!.width.toFloat()
            val height = canvas!!.height.toFloat()
            val diameter = width * this.diameterRatio

            var path1 = Path()
            path1.addRect(0f, 0f, width, height, Path.Direction.CW)
            var path2 = Path()
            path2.addCircle(width / 2, height / 2, diameter / 2, Path.Direction.CW)
            path1.op(path2, Path.Op.DIFFERENCE)
            this.path = path1
            this.path = path
            canvas!!.drawPath(this.path!!, paint)
        }

    }
}