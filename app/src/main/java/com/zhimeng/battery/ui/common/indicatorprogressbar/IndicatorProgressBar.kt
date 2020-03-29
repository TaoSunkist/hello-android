package com.zhimeng.battery.ui.common.indicatorprogressbar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.zhimeng.battery.R
import com.zhimeng.battery.utilities.Dimens
import com.zhimeng.battery.utilities.Dimens.dpToPx

class IndicatorProgressBar : FrameLayout, IProgress {

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    private var roundCornerImageView: RoundCornerImageView
    private var progressBarBackgroundImageView: ImageView
    private var indicatorImageView: AppCompatImageView
    private var maxProgress = 1.0f
    private var progressRadius = 0
    private var progressImage: Drawable? = null
    override var progress = 0.0f
        set(progress) {
            field = progress
            val percent = this.progress / (maxProgress * 1.0f)
            val ivWidth = progressBarBackgroundImageView.width
            //final int initWidth = (int) (ivWidth * 0.08);
            val calcWidth = ivWidth - progressRadius * 2
            val lp = roundCornerImageView.layoutParams as LayoutParams
            val marginEnd = ((1 - percent) * calcWidth).toInt()
            lp.width = ivWidth - marginEnd - Dimens.marginMedium * 2
            roundCornerImageView.layoutParams = lp
            indicatorImageView.x = if (progress == 0f) roundCornerImageView.left.toFloat()
            else roundCornerImageView.right.toFloat() - indicatorImageView.width / 2.2f
        }

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.view_indicator_progress_bar, this)
        indicatorImageView = root.findViewById(R.id.view_indicator_progress_bar_indicator_imageview)
        roundCornerImageView =
            root.findViewById(R.id.view_indicator_progress_bar_roundcornerimageview)
        progressBarBackgroundImageView =
            root.findViewById(R.id.view_indicator_progress_bar_imageview)

        progressBarBackgroundImageView.layoutParams.height =
            roundCornerImageView.layoutParams.height
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ScrewThreadProgressBar)
        val defProgressRadius = dpToPx(10)
        progressRadius = ta.getDimension(
            R.styleable.ScrewThreadProgressBar_stpb_radius,
            defProgressRadius.toFloat()
        ).toInt()
        val progressBackgroundColor = ta.getColor(
            R.styleable.ScrewThreadProgressBar_stpb_background,
            Color.parseColor("#d9d9d9")
        )
        progressImage = ta.getDrawable(R.styleable.ScrewThreadProgressBar_stpb_image)
        maxProgress = 1.0f//ta.getInteger(R.styleable.ScrewThreadProgressBar_stpb_max, 1.0f)
        ta.recycle()
        Log.i(TAG, "defProgressRadius:$defProgressRadius progressRadius:$progressRadius")
        roundCornerImageView.setImageDrawable(progressImage)
        roundCornerImageView.setRadiusPx(progressRadius)

        val drawable = GradientDrawable()
        //设置圆角大小
        drawable.cornerRadius = progressRadius.toFloat()
        //设置边缘线的宽以及颜色
        //drawable.setStroke(1, Color.parseColor("#FF00FF"));
        //设置shape背景色
        drawable.setColor(progressBackgroundColor)
        //设置到TextView中
        progressBarBackgroundImageView.setImageDrawable(drawable)
        progress = 0.0f
    }

    override fun setMax(maxProgress: Float) {
        this.maxProgress = maxProgress
    }

    companion object {
        const val TAG = "ScrewThreadProgressBar"
    }
}