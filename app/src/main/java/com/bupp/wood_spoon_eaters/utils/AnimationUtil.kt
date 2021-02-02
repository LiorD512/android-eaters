package com.bupp.wood_spoon_eaters.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator

class AnimationUtil {

    open class Render (var cx: Context) {
        var du: Long = 1000

        lateinit var animatorSet: AnimatorSet

        fun setAnimation(animatorSet: AnimatorSet) {
            this.animatorSet = animatorSet
        }

        fun setDuration(duration: Long) {
            this.du = duration
        }

        fun start() {
            animatorSet.duration = du
            animatorSet.interpolator = AccelerateInterpolator()
            animatorSet.start()
        }

    }

        fun Swing (view: View) : AnimatorSet {
            val animatorSet = AnimatorSet()

            val object1: ObjectAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, 10f, -10f, 6f, -6f, 3f, -3f, 0f)

            animatorSet.playTogether(object1)
            return animatorSet
        }

    fun Shake (view: View) :AnimatorSet{
        val animatorSet = AnimatorSet()

        val object1:ObjectAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.05f, 0.90f, 1.05f, 1f)
        val object2:ObjectAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.85f, 1.15f, 0.95f, 1f)

        animatorSet.playTogether(object1, object2)
        return animatorSet
    }



}