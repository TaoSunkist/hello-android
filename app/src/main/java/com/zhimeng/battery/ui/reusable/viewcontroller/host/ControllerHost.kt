package com.zhimeng.battery.ui.reusable.viewcontroller.host

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.GestureDetectorCompat
import com.zhimeng.battery.ui.reusable.activity.BaseActivity
import com.zhimeng.battery.ui.reusable.viewcontroller.controller.ViewController
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentationStyle
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentingAnimation
import com.zhimeng.battery.ui.reusable.viewcontroller.util.animation.AnimationEndListener
import com.zhimeng.battery.utilities.SimpleOnGestureListener
import com.zhimeng.battery.utilities.printf
import com.zhimeng.battery.utilities.weak
import kotlin.math.max

interface OnOutsideClickListener {
    fun onOutsideClicked()
}

open class ControllerHost(activity: BaseActivity) : FrameLayout(activity) {

    private var disappearRatio: Float = 0.1f
    private var dimRatio: Float = 0.5f

    private var dimCovers = ArrayList<DimView>()
    private var controllers = ArrayList<ViewController>()
    var onOutsideClickListener: OnOutsideClickListener? by weak()

    var activity: BaseActivity? by weak()
        private set

    init {
        this.activity = activity
        setBackgroundColor(Color.TRANSPARENT)
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
    }

    fun onDestroy() {
        popAll()
        activity = null
    }

    val topViewController: ViewController?
        get() = controllers.lastOrNull()

    @Suppress
    val viewControllers: List<ViewController>
        get() = controllers

    /**
     * True if there is a transaction in progress, push/pop/etc will not work until
     * transaction is done.
     */
    var transactionInProgress: Boolean = false

    /**
     * Add to the bottom of view controller stack. NO animation.
     */
    fun addBottom(viewController: ViewController, activity: BaseActivity) {
        if (viewControllers.any { it.tag == viewController.tag }) {
            /* only allow unique tag */
            return
        }
        viewController.doCreateView(container = this, activity = activity)
        if (!viewController.presentationStyle.fullscreen) {
            throw RuntimeException("You can't add a non-full screen view controller at bottom")
        }
        var layoutParams = LayoutParams(viewController.view.layoutParams).apply {
            width = LayoutParams.MATCH_PARENT
            height = LayoutParams.MATCH_PARENT
        }
        viewController.view.layoutParams = layoutParams
        controllers.add(0, viewController)
        addView(viewController.view, 0)
        measureChild(viewController.view)
        viewController.view.visibility = View.GONE
        DimView(context = context).also { cover ->
            dimCovers.add(0, cover)
            addView(cover, 0)
            if (viewController.presentationStyle.dim) {
                cover.alpha = dimRatio
            }
        }
        printStack()
    }

    /**
     * Push a new view controller to the top of controller stack.
     */
    fun push(
        viewController: ViewController,
        activity: BaseActivity,
        animated: Boolean,
        completion: (() -> Unit)?
    ) {
        if (transactionInProgress) {
            return
        }
        if (viewControllers.any { it.tag == viewController.tag }) {
            /* only allow unique tag */
            return
        }
        printStack()
        transactionInProgress = true
        viewController.doCreateView(container = this, activity = activity)
        var layoutParams = LayoutParams(viewController.view.layoutParams).apply {
            if (viewController.presentationStyle.fullscreen.not()) {
                marginStart = viewController.presentationStyle.minimumSideMargin
                marginEnd = viewController.presentationStyle.minimumSideMargin
                topMargin = viewController.presentationStyle.minimumSideMargin
                bottomMargin = viewController.presentationStyle.minimumSideMargin
            }
            gravity = viewController.presentationStyle.gravity
        }
        viewController.view.layoutParams = layoutParams
        val currentTop = topViewController
        val cover = DimView(context = context).also { cover ->
            dimCovers.add(cover)
            addView(cover)
        }
        addView(viewController.view)
        measureChild(viewController.view)
        viewController.viewWillAppear(animated)
        if (!viewController.presentationStyle.overCurrentContext) {
            currentTop?.viewWillDisappear(animated)
        }

        if (animated) {
            var animators = ArrayList<Animator>()
            animators.add(createPushAnimation(viewController = viewController))
            if (!viewController.presentationStyle.overCurrentContext && currentTop != null) {
                animators.add(
                    createExitAnimation(
                        viewController = currentTop,
                        style = viewController.presentationStyle
                    )
                )
            }
            if (viewController.presentationStyle.dim) {
                animators.add(ObjectAnimator.ofFloat(cover, "alpha", cover.alpha, dimRatio))
            }

            val animatorSet = AnimatorSet()
            animatorSet.addListener(object : AnimationEndListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    bringChildToFront(cover)
                    bringChildToFront(viewController.view)
                    viewController.viewDidAppear(animated)
                    if (!viewController.presentationStyle.overCurrentContext) {
                        currentTop?.viewDidDisappear(animated)
                        currentTop?.view?.visibility = View.GONE
                    }
                    transactionInProgress = false
                    printStack()
                    completion?.invoke()
                }
            })
            animatorSet.duration = viewController.presentationStyle.duration
            animators.forEach { it.duration = viewController.presentationStyle.duration }
            animatorSet.playTogether(animators)
            animatorSet.start()
        } else {
            if (viewController.presentationStyle.dim) {
                cover.alpha = dimRatio
            }
            viewController.viewDidAppear(animated)
            if (!viewController.presentationStyle.overCurrentContext) {
                currentTop?.viewDidDisappear(animated)
            }
            transactionInProgress = false
            printStack()
            completion?.invoke()
        }
        controllers.add(viewController)
    }

    /**
     * Pop every single controllers in the stack. NO ANIMATION.
     */
    fun popAll() {
        topViewController?.viewWillDisappear(animated = false)
        topViewController?.viewDidDisappear(animated = false)
        controllers.reversed().forEach {
            it.doDestroyView(this)
        }
        dimCovers.reversed().forEach {
            removeView(it)
        }
        controllers.clear()
        dimCovers.clear()
    }

    /**
     * Pop to root view controller. NO ANIMATION.
     */
    fun popToRoot() {
        while (controllers.size > 1) {
            pop(animated = false, completion = null)
        }
        printStack()
    }

    /**
     * Pop all view controller except the top one. NO ANIMATION.
     */
    fun popAllButTop() {
        while (controllers.size > 1) {
            val index = controllers.size - 2
            val toPop = controllers.removeAt(index)
            if (toPop.isVisible) {
                toPop.viewWillDisappear(animated = false)
                toPop.viewDidDisappear(animated = false)
            }
            toPop.doDestroyView(this)
            val coverToRemove = dimCovers.removeAt(index)
            removeView(coverToRemove)
        }
        printStack()
    }

    /**
     * Pop the top view controller if there is more than one view controller in stack.
     */
    fun pop(animated: Boolean, completion: (() -> Unit)?): Boolean {
        printStack()
        if (transactionInProgress) {
            return false
        }
        if (controllers.size == 1) {
            return false
        }
        transactionInProgress = true
        val toPop = controllers.removeAt(controllers.size - 1)
        val cover = dimCovers.removeAt(dimCovers.size - 1)
        val newTop = topViewController
        newTop?.view?.visibility = View.VISIBLE
        if (!toPop.presentationStyle.overCurrentContext) {
            newTop?.viewWillAppear(animated)
        }
        if (toPop.isVisible) {
            toPop.viewWillDisappear(animated)
        }
        if (animated) {
            var animators = ArrayList<Animator>()
            animators.add(createPopAnimation(viewController = toPop))
            if (!toPop.presentationStyle.overCurrentContext && newTop != null) {
                animators.add(
                    createEnterAnimation(
                        viewController = newTop,
                        style = toPop.presentationStyle
                    )
                )
            }
            animators.add(ObjectAnimator.ofFloat(cover, "alpha", cover.alpha, 0f))

            val animatorSet = AnimatorSet()
            animatorSet.addListener(object : AnimationEndListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    removeView(cover)
                    if (newTop != null) {
                        bringChildToFront(newTop.view)
                    }
                    if (!toPop.presentationStyle.overCurrentContext) {
                        newTop?.viewDidAppear(animated)
                    }
                    if (toPop.isVisible) {
                        toPop.viewDidDisappear(animated)
                    }
                    toPop.doDestroyView(this@ControllerHost)
                    transactionInProgress = false
                    printStack()
                    completion?.invoke()
                }
            })
            animatorSet.duration = toPop.presentationStyle.duration
            animators.forEach { it.duration = toPop.presentationStyle.duration }
            animatorSet.playTogether(animators)
            animatorSet.start()
        } else {
            if (!toPop.presentationStyle.overCurrentContext) {
                newTop?.viewDidAppear(animated = animated)
            }
            newTop?.view?.apply {
                translationX = 0f
                translationY = 0f
                alpha = 1f
            }
            cover.alpha = 0f
            if (toPop.isVisible) {
                toPop.viewDidDisappear(animated)
            }
            removeView(cover)
            toPop.doDestroyView(this@ControllerHost)
            transactionInProgress = false
            printStack()
            completion?.invoke()
        }
        return true
    }

    open fun onBackPressed(): Boolean {
        if (topViewController?.onBackPressed() == true) {
            return true
        }
        if (topViewController?.presentationStyle?.allowDismiss == false) {
            return true
        }
        if (controllers.size <= 1) {
            return false
        }
        pop(animated = true, completion = null)
        return true
    }

    private fun measureChild(view: View) {
        val lp = view.layoutParams as LayoutParams
        val maxWidth = max(0, measuredWidth - lp.leftMargin - lp.rightMargin)
        val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
            maxWidth,
            if (lp.width == LayoutParams.MATCH_PARENT) MeasureSpec.EXACTLY else MeasureSpec.AT_MOST
        )
        val maxHeight = max(0, measuredHeight - lp.topMargin - lp.bottomMargin)
        val childHeightMeasureSpc = MeasureSpec.makeMeasureSpec(
            maxHeight,
            if (lp.height == LayoutParams.MATCH_PARENT) MeasureSpec.EXACTLY else MeasureSpec.AT_MOST
        )
        view.measure(childWidthMeasureSpec, childHeightMeasureSpc)
    }

    private fun createExitAnimation(
        viewController: ViewController,
        style: PresentationStyle
    ): ObjectAnimator {
        val width = measuredWidth.toFloat()
        val height = measuredHeight.toFloat()

        when (style.animation) {
            PresentingAnimation.RIGHT -> {
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "translationX", 0f, -disappearRatio * width
                )
            }
            PresentingAnimation.RIGHT_REVEAL, PresentingAnimation.RIGHT_TRANSLATION -> {
                bringChildToFront(viewController.view)
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "translationX", 0f, -width
                )
            }
            PresentingAnimation.BOTTOM -> {
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "translationY", 0f, -disappearRatio * height
                )
            }
            PresentingAnimation.BOTTOM_FADE -> {
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "alpha", 1f, 0f
                )
            }
            else -> {
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "alpha", 1f, 0f
                )
            }
        }
    }

    private fun createEnterAnimation(
        viewController: ViewController,
        style: PresentationStyle
    ): ObjectAnimator {
        val width = measuredWidth.toFloat()
        val height = measuredHeight.toFloat()

        when (style.animation) {
            PresentingAnimation.RIGHT -> {
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "translationX", -disappearRatio * width, 0f
                )
            }
            PresentingAnimation.RIGHT_REVEAL, PresentingAnimation.RIGHT_TRANSLATION -> {
                bringChildToFront(viewController.view)
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "translationX", -width, 0f
                )
            }
            PresentingAnimation.BOTTOM -> {
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "translationY", -disappearRatio * height, 0f
                )
            }
            PresentingAnimation.BOTTOM_FADE -> {
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "alpha", 0f, 1f
                )
            }
            else -> {
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "alpha", 0f, 1f
                )
            }
        }
    }

    private fun createPushAnimation(viewController: ViewController): ObjectAnimator {
        val width = measuredWidth.toFloat()
        val height = measuredHeight.toFloat()
        val style = viewController.presentationStyle

        when (style.animation) {
            PresentingAnimation.RIGHT, PresentingAnimation.RIGHT_TRANSLATION -> {
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "translationX", width, 0f
                )
            }
            PresentingAnimation.RIGHT_REVEAL -> {
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "translationX", 0f, 0f
                )
            }
            PresentingAnimation.BOTTOM, PresentingAnimation.BOTTOM_FADE -> {
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "translationY", height, 0f
                )
            }
            else -> {
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "alpha", 0f, 1f
                )
            }
        }
    }

    private fun createPopAnimation(viewController: ViewController): ObjectAnimator {
        val width = measuredWidth.toFloat()
        val height = measuredHeight.toFloat()
        val style = viewController.presentationStyle

        when (style.animation) {
            PresentingAnimation.RIGHT, PresentingAnimation.RIGHT_TRANSLATION -> {
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "translationX", 0f, width
                )
            }
            PresentingAnimation.RIGHT_REVEAL -> {
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "translationX", 0f, 0f
                )
            }
            PresentingAnimation.BOTTOM, PresentingAnimation.BOTTOM_FADE -> {
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "translationY", 0f, height
                )
            }
            else -> {
                return ObjectAnimator.ofFloat(
                    viewController.view,
                    "alpha", 1f, 0f
                )
            }
        }
    }

    override fun addView(child: View?) {
        super.addView(child)
        onlyAllowTopViewToReceiveTouch()
    }

    override fun addView(child: View?, index: Int) {
        super.addView(child, index)
        onlyAllowTopViewToReceiveTouch()
    }

    override fun removeView(view: View?) {
        super.removeView(view)
        onlyAllowTopViewToReceiveTouch()
    }

    /**
     * Make sure only the top view receive touch events,
     * all views under it will ignore any touch
     */
    private fun onlyAllowTopViewToReceiveTouch() {
        if (childCount == 0 || childCount % 2 != 0) {
            return
        }
        for (index in childCount - 1 downTo 0) {
            getChildAt(index).setOnTouchListener { _, _ ->
                true
            }
        }
        getChildAt(childCount - 1).setOnTouchListener(null)
    }

    private fun printStack() {
        printf("===========")
        printf("COUNT", controllers.size, dimCovers.size, childCount)
        printf("Stack     :", controllers)
        printf("Dim Covers:", dimCovers)
        printf("===========")
    }

    private var testRect = Rect()
    private val detector = GestureDetectorCompat(context, object : SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            val it = topViewController?.presentationStyle
            if (it != null) {
                if (onOutsideClickListener != null) {
                    onOutsideClickListener?.onOutsideClicked()
                } else if (it.allowDismiss && it.fullscreen.not() && it.allowTapOutsideToDismiss) {
                    pop(animated = true, completion = null)
                }
            }
            return true
        }
    })

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (childCount == 0 || ev == null) {
            return false
        }
        getChildAt(childCount - 1).getHitRect(testRect)
        if (testRect.contains(ev.x.toInt(), ev.y.toInt())) {
            super.dispatchTouchEvent(ev)
        } else {
            detector.onTouchEvent(ev)
        }
        return true
    }
}

private class DimView(context: Context) : View(context) {

    init {
        layoutParams =
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        setBackgroundColor(Color.BLACK)
        alpha = 0f
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }
}