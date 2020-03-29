package com.zhimeng.battery.ui.reusable.viewcontroller.presentation

import android.view.Gravity

enum class PresentingAnimation {
    RIGHT,
    RIGHT_REVEAL,
    RIGHT_TRANSLATION,
    BOTTOM,
    FADE,
    BOTTOM_FADE
}

data class PresentationStyle(
    var animation: PresentingAnimation = PresentingAnimation.RIGHT,
    var dim: Boolean = true,
    var duration: Long = 250,
    var fullscreen: Boolean = true,
    var gravity: Int = Gravity.TOP,
    var overCurrentContext: Boolean = false,
    var allowDismiss: Boolean = true,
    var allowTapOutsideToDismiss: Boolean = true,
    var minimumSideMargin: Int = 0
)