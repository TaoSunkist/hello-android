package com.zhimeng.battery.utilities

import android.annotation.TargetApi
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.res.ResourcesCompat
import java.util.*

fun getDrawable(resources: Resources, res: Int, dimension: Int): Drawable? {
    val drawable = ResourcesCompat.getDrawable(resources, res, null)
    drawable?.setBounds(0, 0, dimension, dimension)
    return drawable
}

fun getDrawable(resources: Resources, res: Int, width: Int, height: Int): Drawable? {
    val drawable = ResourcesCompat.getDrawable(resources, res, null)
    drawable?.setBounds(0, 0, width, height)
    return drawable
}

fun getColor(resources: Resources, res: Int): Int {
    return ResourcesCompat.getColor(resources, res, null)
}

@TargetApi(Build.VERSION_CODES.N)
fun getCurrentLocale(resources: Resources): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales.get(0)
    } else {
        resources.configuration.locale
    }
}