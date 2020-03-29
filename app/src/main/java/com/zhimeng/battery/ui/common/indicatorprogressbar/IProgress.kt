package com.zhimeng.battery.ui.common.indicatorprogressbar

interface IProgress {
    fun setMax(maxProgress: Float)

    var progress: Float
}