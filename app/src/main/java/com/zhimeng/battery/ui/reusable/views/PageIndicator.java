package com.zhimeng.battery.ui.reusable.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.zhimeng.battery.R;
import com.zhimeng.battery.utilities.ColorUtils;


/**
 * Created by EagleDiao on 2016-07-12.
 */

public class PageIndicator extends LinearLayout {

    private View[] mDots;
    private DotDrawable[] mDotDrawables;
    private int mDefaultDotColor;
    private int mSelectedDotColor;
    private int mRealItemCount;
    private int mDotSize;
    private int mDotMargin;
    private int mCurrentPage;
    private float mCurrentOffset;

    public PageIndicator(Context context) {
        super(context);
        commonInit(context, null);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        commonInit(context, attrs);
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        commonInit(context, attrs);
    }

    private void commonInit(final Context context, final AttributeSet attributeSet) {
        mDefaultDotColor = Color.WHITE;
        mSelectedDotColor = Color.RED;
        mDotSize = 24;
        mDotMargin = 8;
        if (attributeSet != null) {
            final TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.PageIndicator);
            mDefaultDotColor = a.getColor(R.styleable.PageIndicator_default_color, mDefaultDotColor);
            mSelectedDotColor = a.getColor(R.styleable.PageIndicator_selected_color, mSelectedDotColor);
            mDotSize = a.getDimensionPixelSize(R.styleable.PageIndicator_dot_size, mDotSize);
            mDotMargin = a.getDimensionPixelSize(R.styleable.PageIndicator_dot_margin, mDotMargin);
            a.recycle();
        }
    }

    public void setDefaultDotColor(int color) {
        mDefaultDotColor = color;
    }

    public void setDotSize(int size) {
        mDotSize = size;
    }

    public void setDotMargin(int size) {
        mDotMargin = size;
    }

    public void setSelectedDotColor(int color) {
        mSelectedDotColor = color;
    }

    private LayoutParams obtainLayoutParams() {
        final int size = mDotSize;
        final int margin = mDotMargin;
        final LayoutParams lp = new LayoutParams(size, size);
        if (getOrientation() == LinearLayout.HORIZONTAL) {
            lp.gravity = Gravity.CENTER_VERTICAL;
            lp.leftMargin = margin;
            lp.rightMargin = margin;
        } else {
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            lp.topMargin = margin;
            lp.bottomMargin = margin;
        }
        return lp;
    }

    public void setPageCount(final int itemCount) {
        mRealItemCount = itemCount;
        mDotDrawables = new DotDrawable[itemCount];
        mDots = new View[itemCount];
        for (int i = 0; i < itemCount; i++) {
            final View view = new View(getContext());
            mDotDrawables[i] = new DotDrawable(i == 0 ? mSelectedDotColor : mDefaultDotColor);
            mDots[i] = view;
            view.setBackground(mDotDrawables[i]);
            view.setLayoutParams(obtainLayoutParams());
            addView(view);
        }
    }

    public void onPageScrolled(int position, float positionOffset) {
        final int adapterPosition = position;
        position = position % mRealItemCount;
        mCurrentPage = position;
        mCurrentOffset = positionOffset;
        for (int i = 0; i < mDotDrawables.length; i++) {
            final boolean colorChanged;
            if (i == mCurrentPage) {
                colorChanged = mDotDrawables[i].setColor(ColorUtils.INSTANCE.getTransitionColor(mSelectedDotColor, mDefaultDotColor, 1 - mCurrentOffset));
            } else if (i == mCurrentPage + 1 || ((adapterPosition > mRealItemCount) && (i == (mCurrentPage + 1) % mRealItemCount))) {
                colorChanged = mDotDrawables[i].setColor(ColorUtils.INSTANCE.getTransitionColor(mSelectedDotColor, mDefaultDotColor, mCurrentOffset));
            } else {
                colorChanged = mDotDrawables[i].setColor(mDefaultDotColor);
            }
            if (colorChanged) {
                mDots[i].invalidate();
            }
        }
    }

    public class DotDrawable extends ShapeDrawable {

        private int mColor;

        public DotDrawable(final int color) {
            super(new OvalShape());
        }

        @Override
        protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
            paint.setColor(mColor);
            super.onDraw(shape, canvas, paint);
        }

        public boolean setColor(final int color) {
            if (mColor == color) {
                return false;
            }
            mColor = color;
            return true;
        }
    }
}
