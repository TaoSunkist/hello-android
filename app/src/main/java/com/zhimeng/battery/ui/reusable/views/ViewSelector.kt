package com.zhimeng.battery.ui.reusable.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.jakewharton.rxrelay2.PublishRelay

class ViewSelector : ViewPager {

    var allowUserInteraction: Boolean = true
    val onPageSelectedRelay = PublishRelay.create<Int>()

    constructor(context: Context) : super(context) {
        commonInit(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        commonInit(context, attrs)
    }

    private fun commonInit(context: Context, attrs: AttributeSet?) {
        addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                onPageSelectedRelay.accept(position)
            }
        })
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (allowUserInteraction) {
            return super.onInterceptTouchEvent(event)
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (allowUserInteraction) {
            return super.onTouchEvent(event)
        }
        return false
    }

    val currentPageIndex: Int
        get() = currentItem

    fun setViews(views: List<View>) {
        adapter = ViewSelectorAdapter(views = views)
    }

    fun setPageIndex(index: Int) {
        setCurrentItem(index, true)
    }

    private class ViewSelectorAdapter(views: List<View>) : PagerAdapter() {
        var views = views

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = views[position]
            container.addView(view, 0)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as? View
            if (view != null) {
                container.removeView(view)
            }
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return views.size
        }
    }
}



