package com.zhimeng.battery.utilities

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.regex.Pattern
import kotlin.math.min

val String.isDigit: Boolean
    get() = this.matches("\\d+".toRegex())

val String.isValidPhoneNumber: Boolean
    get() = this.isDigit && (this.length == 11 || this.length == 12)

/* 判断是否只包含数字/英文/汉字 */
val String.isLetterDigitOrChinese: Boolean
    get() = !this.matches("^[a-z0-9A-Z\u4e00-\u9fa5]+$".toRegex())

/* 判断是否包含了特殊字符 */
val String.isSpecialCharacter: Boolean
    get() {
        val regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t"
        val p = Pattern.compile(regEx)
        val m = p.matcher(this)
        return m.find()
    }

fun isNewerVersion(current: String, version: String?): Boolean {
    if (version == null) {
        return false
    }
    val left = current.split(".")
    val right = version.split(".")

    for (i in 0 until min(left.size, right.size)) {
        val d1 = left[i].toIntOrNull() ?: 0
        val d2 = right[i].toIntOrNull() ?: 0
        if (d1 > d2) {
            return false
        } else if (d1 < d2) {
            return true
        }
    }
    return if (left.size > right.size) {
        false
    } else {
        false
    }
}

fun getFormatNumber(value: Long, scale: String): String {
    val bigDecimal = BigDecimal(value)
    val decimal = bigDecimal.divide(BigDecimal(scale))
    val format = "###.#"
    val formater = DecimalFormat(format)
    formater.roundingMode = RoundingMode.HALF_UP
    return formater.format(decimal)
}