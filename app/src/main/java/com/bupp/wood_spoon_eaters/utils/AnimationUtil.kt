package com.bupp.wood_spoon_eaters.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator

class AnimationUtil {


    fun shakeView(view: View) {
        ObjectAnimator.ofFloat(
            view, "translationX",
            0f, -30f, 30.0f, -15.0f, 15.0f, -5.0f, 5.0f, 0f,
        ).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = 0
            start()
        }
    }

//    fun enterFromRightWithAlpha(view: View, customDuration: Long = 500, customStartDelay: Long = 150) {
//        view.alpha = 0f
//        ObjectAnimator.ofFloat(
//            view, "translationX",
//            250f, 0f,
//        ).apply {
//            duration = customDuration
//            interpolator = AccelerateDecelerateInterpolator()
//            startDelay = customStartDelay
//            repeatCount = 0
//            start()
//        }
//
//        ObjectAnimator.ofFloat(
//            view, "alpha",
//            0f, 1f,
//        ).apply {
//            duration = customDuration / 2
//            interpolator = AccelerateInterpolator()
//            startDelay = customStartDelay
//            repeatCount = 0
//            start()
//        }
//    }

    fun enterFromBottomWithAlpha(view: View, customDuration: Long = 500, customStartDelay: Long = 150) {
        view.alpha = 0f
        ObjectAnimator.ofFloat(
            view, "translationY",
            250f, 0f,
        ).apply {
            duration = customDuration
            interpolator = OvershootInterpolator()
            startDelay = customStartDelay
            repeatCount = 0
            start()
        }

        ObjectAnimator.ofFloat(
            view, "alpha",
            0f, 1f,
        ).apply {
            duration = (customDuration / 1.5).toLong()
            interpolator = AccelerateInterpolator()
            startDelay = customStartDelay
            repeatCount = 0
            start()
        }
    }

    fun exitToBottomWithAlpha(view: View, customDuration: Long = 500, customStartDelay: Long = 150) {
        view.alpha = 1f
        ObjectAnimator.ofFloat(
            view, "translationY",
            0f, 250f,
        ).apply {
            duration = customDuration
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = customStartDelay
            repeatCount = 0
            start()
        }

        ObjectAnimator.ofFloat(
            view, "alpha",
            1f, 0f,
        ).apply {
            duration = customDuration / 2
            interpolator = AccelerateInterpolator()
            startDelay = customStartDelay
            repeatCount = 0
            start()
        }
    }

//    fun slideHorizontalFromTo(view: View, from: Float, to: Float, customDuration: Long = 500, customStartDelay: Long = 150) {
//        view.alpha = 0f
//        ObjectAnimator.ofFloat(
//            view, "translationX",
//            from, to,
//        ).apply {
//            duration = customDuration
//            interpolator = AccelerateDecelerateInterpolator()
//            startDelay = customStartDelay
//            repeatCount = 0
//            start()
//        }
//    }
//
//    fun slideVerticalFromTo(view: View, from: Float, to: Float, customDuration: Long = 500, customStartDelay: Long = 150) {
//        view.alpha = 0f
//        ObjectAnimator.ofFloat(
//            view, "translationY",
//            from, to,
//        ).apply {
//            duration = customDuration
//            interpolator = AccelerateDecelerateInterpolator()
//            startDelay = customStartDelay
//            repeatCount = 0
//            start()
//        }
//    }

    fun alphaIn(view: View, customDuration: Long = 250, customStartDelay: Long = 150) {
        if(view.alpha == 0f){
            view.alpha = 0f
            ObjectAnimator.ofFloat(
                view, "alpha",
                0f, 1f,
            ).apply {
                duration = customDuration
                interpolator = AccelerateInterpolator()
                startDelay = customStartDelay
                repeatCount = 0
                start()
            }
        }
    }

    fun alphaOut(view: View, customDuration: Long = 250, customStartDelay: Long = 0, listener: Animator.AnimatorListener? = null) {
        view.alpha = 1f
        ObjectAnimator.ofFloat(
            view, "alpha",
            1f, 0f,
        ).apply {
            duration = customDuration
            interpolator = AccelerateInterpolator()
            startDelay = customStartDelay
            repeatCount = 0
            listener?.let{
                addListener(listener)
            }
            start()
        }
    }


}