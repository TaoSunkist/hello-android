package com.zhimeng.battery.utilities

import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

class SpannableStringBuilderCompat {

    private val sb = SpannableStringBuilder()

    fun append(text: CharSequence, what: Any, flags: Int): SpannableStringBuilderCompat {
        val start = sb.length
        sb.append(text)
        sb.setSpan(what, start, sb.length, flags)
        return this
    }

    fun append(
        text: CharSequence,
        what: Array<Any>,
        flags: IntArray
    ): SpannableStringBuilderCompat {
        if (what.size != flags.size) throw IllegalArgumentException("Array ‘ what ’ must be the same length as ‘flags’ array.")

        val start = sb.length
        sb.append(text)
        for (i in what.indices) {
            sb.setSpan(what[i], start, sb.length, flags[i])
        }
        return this
    }

    fun append(text: String): SpannableStringBuilderCompat {
        sb.append(text)
        return this
    }

    fun toSpannable(): SpannableStringBuilder = sb

    companion object {
        fun singleSpan(text: CharSequence?, what: Any): Spannable {
            val spannable = SpannableString(text)
            spannable.setSpan(what, 0, spannable.length, 0)
            return spannable
        }
    }
}

class ShadowSpan(dx: Float, dy: Float, radius: Float, color: Int) : ClickableSpan() {

    var dx: Float = dx
    var dy: Float = dy
    var radius: Float = radius
    var color: Int = color

    override fun updateDrawState(ds: TextPaint) {
        ds.setShadowLayer(radius, dx, dy, color)
    }

    override fun onClick(widget: View) {
    }
}