package me.taosunkist.hello.ui.squareprogressbar

import android.graphics.Color
import android.graphics.Paint.Align

class PercentStyle(var align: Align, var textSize: Float, percentSign: Boolean) {
    var isPercentSign = percentSign
    var customText = "%"
    var textColor = Color.BLACK
}