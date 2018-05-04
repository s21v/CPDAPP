package com.cpd.yuqing.behavior

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View

/**
 * Created by s21v on 2018/5/2.
 */
class FabHideBehavior(context: Context, attributeSet: AttributeSet) :
        CoordinatorLayout.Behavior<View>(context, attributeSet) {
    private var dx = 0f
    private val duration = 500L
    private var isAnimating = false
    private var isShow = true

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View,
                                     directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        val childLeft = child.left.toFloat()
        val parentRight = coordinatorLayout.right.toFloat()
        dx = parentRight - childLeft
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (dy > 0 && !isAnimating && isShow) {  // 手指向上滑动，画面下移，开启隐藏动画
            hide(child)
        } else if (dy < 0 && !isAnimating && !isShow) {
            show(child)
        }
    }

    private fun hide(child: View) {
        val animatorSet = AnimatorSet()
        val animator1 = ObjectAnimator.ofFloat(child, "translationX", child.translationX, dx)
        val animator2 = ObjectAnimator.ofFloat(child, "scaleX", 1f, 0f)
        val animator3 = ObjectAnimator.ofFloat(child, "scaleY", 1f, 0f)
        val animator4 = ObjectAnimator.ofFloat(child, "alpha", 1f, 0f)
        val animator5 = ObjectAnimator.ofFloat(child, "rotation", 0f, 360f)
        animatorSet.playTogether(animator1, animator2, animator3, animator4, animator5)
        animatorSet.duration = duration
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                isAnimating = false
                isShow = false
            }

            override fun onAnimationStart(animation: Animator?) {
                isAnimating = true
            }
        })
        animatorSet.start()
    }

    private fun show(child: View) {
        if (child.visibility == View.INVISIBLE)
            child.visibility = View.VISIBLE
        val animatorSet = AnimatorSet()
        val animator1 = ObjectAnimator.ofFloat(child, "translationX", child.translationX, child.translationX - dx)
        val animator2 = ObjectAnimator.ofFloat(child, "scaleX", 0f, 1f)
        val animator3 = ObjectAnimator.ofFloat(child, "scaleY", 0f, 1f)
        val animator4 = ObjectAnimator.ofFloat(child, "alpha", 0f, 1f)
        val animator5 = ObjectAnimator.ofFloat(child, "rotation", 0f, 360f)
        animatorSet.playTogether(animator1, animator2, animator3, animator4, animator5)
        animatorSet.duration = duration
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                isAnimating = false
                isShow = true
            }

            override fun onAnimationStart(animation: Animator?) {
                isAnimating = true
            }
        })
        animatorSet.start()
    }

    companion object {
        private const val TAG = "FabHideBehavior"
    }
}