package com.zhimeng.battery.utilities

class Optional<T>(t: T? = null) {

    companion object {
        fun <T> ofNull(): Optional<T> {
            return Optional()
        }
    }

    var value: T? = t

    val exists: Boolean
        get() = value != null

}