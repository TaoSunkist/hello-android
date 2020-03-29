package com.zhimeng.battery.ui.reusable.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import com.zhimeng.battery.R
import com.zhimeng.battery.utilities.Dimens
import com.zhimeng.battery.utilities.weak

interface PhotoShareMenuViewDelegate {
    fun photoShareMenuViewDidSelectShare(shareType: ShareType)
}

enum class ShareType(@DrawableRes val id: Int, val tag: String) {
    SAVE(R.drawable.ic_download_image, "SAVE"),
    WECHAT_FRIENDS(R.drawable.ic_share_to_wx_friends, "WECHAT_FRIENDS"),
    WECHAT(R.drawable.ic_share_to_wechat, "WECHAT"),
    SINA(R.drawable.ic_share_to_sina, "SINA"),
    QQ(R.drawable.ic_share_to_qq, "QQ")
}

class PhotoShareMenuView : LinearLayout, View.OnClickListener {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val shareMenuPanel = LinearLayout(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    var delegate: PhotoShareMenuViewDelegate? by weak()

    init {
        for (type in ShareType.values()) {
            shareMenuPanel.addView(FrameLayout(context).apply {
                tag = type
                layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT).apply { weight = 1f }
                val iconImageView = AppCompatImageView(context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER
                        setPadding(
                            Dimens.marginMedium,
                            Dimens.marginMedium,
                            Dimens.marginMedium,
                            Dimens.marginMedium
                        )
                    }
                }
                iconImageView.setImageResource(type.id)
                addView(iconImageView)
                setOnClickListener(this@PhotoShareMenuView)
            })
        }
        addView(shareMenuPanel)
    }

    override fun onClick(v: View) {
        (v.tag as? ShareType)?.let { tag ->
            delegate?.photoShareMenuViewDidSelectShare(tag)
        }
    }
}
