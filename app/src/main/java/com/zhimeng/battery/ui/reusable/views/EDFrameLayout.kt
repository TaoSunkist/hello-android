package com.zhimeng.battery.ui.reusable.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class EDFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyle, defStyleRes) {

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (isEnabled) {
            return super.dispatchTouchEvent(ev)
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isEnabled) {
            return super.onTouchEvent(event)
        }
        return false
    }
}