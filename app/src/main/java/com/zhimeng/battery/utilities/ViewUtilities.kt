package com.zhimeng.battery.utilities

import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.squareup.picasso.Picasso
import com.zhimeng.battery.R
import com.zhimeng.battery.ui.reusable.uimodels.ImageUIModel

fun View.setFrame(x: Int, y: Int, width: Int, height: Int) {
    layout(x, y, x + width, y + height)
}

fun View.makeExactlyMeasure(dimension: Int): Int {
    return View.MeasureSpec.makeMeasureSpec(dimension, View.MeasureSpec.EXACTLY)
}

fun View.makeAtMostMeasure(dimension: Int): Int {
    return View.MeasureSpec.makeMeasureSpec(dimension, View.MeasureSpec.AT_MOST)
}

fun AppCompatTextView.setDrawableLeft(drawable: Drawable?) {
    setCompoundDrawables(drawable, null, null, null)
}

fun AppCompatTextView.setDrawableRight(drawable: Drawable?) {
    setCompoundDrawables(null, null, drawable, null)
}

fun AppCompatImageView.bind(model: ImageUIModel) {
    when {
        model.imageRes != null -> setImageResource(model.imageRes!!)
        model.imageUrl?.isNotEmpty() ?: false -> {
            Picasso.get().load(model.imageUrl)
                .placeholder(model.placeholder ?: R.drawable.placeholder_image).apply {
                    if (model.targetWidth > 0 || model.targetHeight > 0) {
                        resize(model.targetWidth, model.targetHeight)
                        onlyScaleDown()
                    }
                    into(this@bind)
                }
        }
        model.placeholder != null -> {
            setImageResource(model.placeholder!!)
        }
    }
}
