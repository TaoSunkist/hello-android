package com.zhimeng.battery.ui.reusable.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhimeng.battery.R;

/**
 * Created by Sunkist on 2016/3/29.
 * 自定义的全局TitleBar
 */
public class TitleBar extends LinearLayout {
    /**
     * 中间文本
     */
    private TextView title;
    /**
     * 和主要内容的风分割线
     */
    private View mContentLine;

    private ObjectAnimator mFadeOutAnimator;
    /**
     * TitleBar的背景色
     */
    private int backgroundColor;
    /**
     * 中心标题的字体大小
     */
    private float titleSize;
    /**
     * 中心标题的字体颜色
     */
    private int titleColor;
    /**
     * 右边的按钮设置
     */
    private ImageView rightBtn;

    /***
     * 左边按钮设置
     *
     * @param context
     */
    private ImageView leftBtn;
    /***
     * 右边文字设置
     */
    private TextView rightText, leftText;
    private RelativeLayout leftLayout;
    private RelativeLayout mTitleBarWrapContent;

    public TitleBar(Context context) {
        super(context);
        initView(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.uikit_title_bar, this);
        title = (TextView) findViewById(R.id.title);
        rightBtn = (ImageView) findViewById(R.id.right_image);
        rightText = (TextView) findViewById(R.id.right_text);
        leftText = (TextView) findViewById(R.id.left_text);
        mContentLine = findViewById(R.id.line);
        leftLayout = (RelativeLayout) findViewById(R.id.left_layout);
        leftBtn = (ImageView) findViewById(R.id.left_image);
        mTitleBarWrapContent = (RelativeLayout) findViewById(R.id.title_bar_wrap_content_rl);
        backgroundColor = context.getResources().getColor(R.color.uikit_title_bar_bg_color);
        titleColor = context.getResources().getColor(R.color.uikit_title_bar_title_color);
        titleSize = context.getResources().getDimensionPixelSize(R.dimen.uikit_title_font_size);
        if (attrs != null) {
            parseStyle(context, attrs);
        }
    }

    private void parseStyle(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.title_bar);

        String titleStr = typedArray.getString(R.styleable.title_bar_title_text);

        Drawable leftDrawable = typedArray.getDrawable(R.styleable.title_bar_left_image);
        titleSize = typedArray.getFloat(R.styleable.title_bar_title_size, titleSize);
        titleColor = typedArray.getColor(R.styleable.title_bar_title_color, titleColor);
        backgroundColor = typedArray.getColor(R.styleable.title_bar_background_color, backgroundColor);
        Drawable rightDrawable = typedArray.getDrawable(R.styleable.title_bar_right_image);
        String rightTextContent = typedArray.getString(R.styleable.title_bar_right_text);
        int mRightTextColor = typedArray.getColor(R.styleable.title_bar_right_text_color, rightText.getCurrentTextColor());
        int mLeftTextColor = typedArray.getColor(R.styleable.title_bar_left_text_color, leftText.getCurrentTextColor());
        float mRightTextSize = typedArray.getDimension(R.styleable.title_bar_right_text_size, rightText.getTextSize());
        float mLeftTextSize = typedArray.getDimension(R.styleable.title_bar_left_text_size, leftText.getTextSize());
        boolean isDividerLineEnable = typedArray.getBoolean(R.styleable.title_bar_line_enable, true);
        String leftText = typedArray.getString(R.styleable.title_bar_left_text);

        typedArray.recycle();

        this.rightText.setTextColor(mRightTextColor);
        this.rightText.setText(rightTextContent);
        this.rightText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRightTextSize);

        this.leftText.setText(leftText);
        this.leftText.setTextColor(mLeftTextColor);
        this.leftText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLeftTextSize);

        this.mContentLine.setVisibility(isDividerLineEnable ? VISIBLE : GONE);

        title.setText(titleStr);
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        title.setTextColor(titleColor);
        rightBtn.setImageDrawable(rightDrawable);

        leftBtn.setImageDrawable(leftDrawable);
        mTitleBarWrapContent.setBackgroundColor(backgroundColor);
    }

    public void RightBtnClick(Context context, OnClickListener mOnClickListener, int visible) {
        rightBtn.setOnClickListener(mOnClickListener);
        rightBtn.setVisibility(visible);
    }

    /**
     * @param onClickListener
     * @description 右边按钮的点击事件
     */
    public TitleBar bindRightImageClicker(OnClickListener onClickListener) {
        rightBtn.setOnClickListener(onClickListener);
        return this;
    }


    /**
     * @param onClickListener
     * @description 左边按钮的点击事件
     */
    public TitleBar onClickerToLeft(OnClickListener onClickListener) {
        leftText.setOnClickListener(onClickListener);
        leftBtn.setOnClickListener(onClickListener);
        leftLayout.setOnClickListener(onClickListener);
        return this;
    }


    public TitleBar enableRightImage() {
        rightBtn.setVisibility(View.VISIBLE);
        return this;
    }

    public TitleBar setRightImage(Drawable drawable) {
        rightBtn.setImageDrawable(drawable);
        return this;
    }

    public ImageView getLeftBtn() {
        return leftBtn;
    }

    public TitleBar setRightImageClicker(OnClickListener onClickListener) {
        rightBtn.setOnClickListener(onClickListener);
        return this;
    }

    public ImageView getRightBtn() {
        return rightBtn;
    }


    public TitleBar setTitleBarText(String titles) {
        title.setText(titles);
        return this;
    }

    public TitleBar setTitle(String content) {
        title.setText(content);
        return this;
    }

    /***
     * 设置右边文字
     *
     * @param str
     * @return
     */
    public TextView setRightText(String str) {
        rightText.setText(str);
        return rightText;
    }
}
