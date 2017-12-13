package com.cpd.yuqing.behavior

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * Created by s21v on 2017/8/1.
 */
class bottomShowHideBehavior(context: Context?, attrs: AttributeSet?) : CoordinatorLayout.Behavior<View>(context, attrs) {
    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
//        Log.i(TAG, "onStartNestedScroll")
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL  //滚动方向
    }

    var translationDistance = 0f    //底边栏移动的距离

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (type == ViewCompat.TYPE_TOUCH) {
            translationDistance = child.translationY
//            Log.i(TAG, "onNestedPreScroll dy=$dy, translationDistance:$translationDistance, child height:" + child.height)
            if (dy > 0) //手指向上滑动，底边栏消失(向下移动)
            {
                if (translationDistance < child.height) {
                    translationDistance += dy
                    if (translationDistance > child.height)
                        translationDistance = child.height.toFloat()
                    child.translationY = translationDistance
                }
            } else  //手指向下滑动，底边栏显示(向上移动)
                if (translationDistance > 0) {
                    translationDistance += dy
                    if (translationDistance < 0)
                        translationDistance = 0f
                    child.translationY = translationDistance
                }
        }
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, type: Int) {
        if (type == ViewCompat.TYPE_TOUCH) {
            if (translationDistance > child.height/2 && translationDistance < child.height) {
                translationDistance = child.height.toFloat()
                child.translationY = translationDistance
            } else if (translationDistance > 0 && translationDistance <= child.height/2){
                translationDistance = 0f
                child.translationY = translationDistance
            }
        }
    }

    companion object {
        private val TAG: String = "bottomShowHideBehavior"
    }
}