package com.zhimeng.battery.utilities

import java.util.*

object DateUtilities {

    fun beginningOfDayTime(useUTC: Boolean = false): Date {
        val cal =
            if (useUTC) Calendar.getInstance(TimeZone.getTimeZone("UTC")) else Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }
}

fun Calendar.getFormatterMinute(): String {
    return String.format("%02d", this.get(Calendar.MINUTE))
}

fun Calendar.getFormatterHour(): String {
    return String.format(
        "%02d",
        if (this.get(Calendar.AM_PM) == 1 && this.get(Calendar.HOUR) == 0) {
            12
        } else {
            this.get(Calendar.HOUR)
        }
    )
}