package com.zhimeng.battery.utilities

import android.graphics.Color

import androidx.annotation.ColorInt

/**
 * Created by EagleDiao on 2016-02-15.
 */
object ColorUtils {

    /**
     * @param color
     * @param degree
     * @return
     */
    fun getDarkenColor(color: Int, degree: Float): Int {
        var degree = degree
        val r = Color.red(color)
        val b = Color.blue(color)
        val g = Color.green(color)
        degree = 1 - degree
        return Color.rgb((r * degree).toInt(), (g * degree).toInt(), (b * degree).toInt())
    }

    /**
     * @param color
     * @param alpha
     * @return
     */
    fun getColorWithAlpha(color: Int, alpha: Float): Int {
        val r = Color.red(color)
        val b = Color.blue(color)
        val g = Color.green(color)
        return Color.argb((255 * alpha).toInt(), r, g, b)
    }

    /**
     * @param degree
     * @return
     */
    fun getWhiteColor(degree: Float): Int {
        val comp = (255 * degree).toInt()
        return Color.rgb(comp, comp, comp)
    }

    /**
     * Produce true if given color is light.
     *
     * @param color color
     * @return true if given color is light, false the color is light
     */
    fun isLightColor(@ColorInt color: Int): Boolean {
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness < 0.5
    }

    /**
     * Produce the average of two colors.
     *
     * @param color1 color1
     * @param color2 color2
     * @return average of two colors.
     */
    @ColorInt
    fun getAverageColor(
        @ColorInt color1: Int,
        @ColorInt color2: Int
    ): Int {

        return getTransitionColor(color1, color2, 0.5f)
    }

    /**
     * Produce a color in between two colors with given step.
     *
     * @param fromColor from color
     * @param toColor   to color
     * @param step      step (0 - 1)
     * @return a transition color
     */
    @ColorInt
    fun getTransitionColor(@ColorInt fromColor: Int, @ColorInt toColor: Int, step: Float): Int {

        val fromRed = Color.red(fromColor)
        val fromGreen = Color.green(fromColor)
        val fromBlue = Color.blue(fromColor)

        val toRed = Color.red(toColor)
        val toGreen = Color.green(toColor)
        val toBlue = Color.blue(toColor)

        return Color.argb(
            255,
            (fromRed * step + toRed * (1 - step)).toInt(),
            (fromGreen * step + toGreen * (1 - step)).toInt(),
            (fromBlue * step + toBlue * (1 - step)).toInt()
        )
    }
}
