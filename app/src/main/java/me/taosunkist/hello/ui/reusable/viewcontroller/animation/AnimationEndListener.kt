package me.taosunkist.hello.ui.reusable.viewcontroller.animation

import android.animation.Animator

abstract class AnimationEndListener : Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator?) {
    }

    override fun onAnimationCancel(animation: Animator?) {
    }

    override fun onAnimationStart(animation: Animator?) {
    }
}