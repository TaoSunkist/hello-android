package com.zhimeng.battery.utilities

import android.app.Activity
import android.graphics.Rect
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxrelay2.BehaviorRelay

class KeyboardObserver(activity: AppCompatActivity) {
    companion object {
        const val KEY_KEYBOARD_HEIGHT = "KnownKeyboardHeight"
    }

    private var rect = Rect()
    private var lastHeight: Int = 0
    private var minKeyboardHeight = Dimens.dpToPx(150)
    private var context: AppCompatActivity? by weak()

    val keyboardHeight: BehaviorRelay<Int>
    val keyboardVisible: BehaviorRelay<Boolean>

    init {
        context = activity
        /* if we never stored any keyboard height, we "guess" roughly what it will be
         * only for the first time */
        val stored = activity.getPreferences(Activity.MODE_PRIVATE)
            .getInt(KEY_KEYBOARD_HEIGHT, Dimens.dpToPx(240))
        keyboardHeight = BehaviorRelay.createDefault(stored)
        keyboardVisible = BehaviorRelay.createDefault(false)

        activity.window.decorView
            .findViewById<View>(android.R.id.content)
            .viewTreeObserver.addOnGlobalLayoutListener {
            context?.window?.decorView?.getWindowVisibleDisplayFrame(rect)
            val diff = lastHeight - rect.height()
            when {
                lastHeight == 0 -> lastHeight = rect.height()
                diff > minKeyboardHeight -> {
                    if (keyboardVisible.value == false) {
                        keyboardVisible.accept(true)
                    }
                    if (diff != keyboardHeight.value) {
                        keyboardHeight.accept(diff)
                        context?.getPreferences(Activity.MODE_PRIVATE)?.edit()
                            ?.putInt(KEY_KEYBOARD_HEIGHT, diff)?.apply()
                    }
                }
                else -> {
                    if (keyboardVisible.value == true) {
                        keyboardVisible.accept(false)
                    }
                    lastHeight = rect.height()
                }
            }
        }
    }
}