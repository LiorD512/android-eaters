package com.bupp.wood_spoon_chef.utils

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator

class AnimationUtil {

    fun shakeView(view: View, context: Context) {
        Utils.vibrate(context)
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

    fun enterFromRight(view: View) {
        ObjectAnimator.ofFloat(
                view, "translationX",
                100f, 0f,
        ).apply {
            duration = 500
            interpolator = BounceInterpolator()
            repeatCount = 0
            start()
        }

        ObjectAnimator.ofFloat(
                view, "alpha",
                0f, 1f,
        ).apply {
            duration = 250
            interpolator = AccelerateInterpolator()
            repeatCount = 0
            start()
        }
    }

    fun exitToRight(view: View) {
        ObjectAnimator.ofFloat(
                view, "translationX",
                0f, 150f,
        ).apply {
            duration = 500
            interpolator = AccelerateInterpolator()
            repeatCount = 0
            start()
        }

        ObjectAnimator.ofFloat(
                view, "alpha",
                1f, 0f,
        ).apply {
            duration = 250
            interpolator = AccelerateInterpolator()
            repeatCount = 0
            start()
        }
    }


    fun alphaIn(view: View) {
        if(view.alpha <= 0){
            ObjectAnimator.ofFloat(
                    view, "alpha",
                    0f, 1f,
            ).apply {
                duration = 250
                interpolator = AccelerateInterpolator()
                repeatCount = 0
                start()
            }
        }
    }

    fun alphaOut(view: View) {
        if(view.alpha >= 1){
            ObjectAnimator.ofFloat(
                    view, "alpha",
                    1f, 0f,
            ).apply {
                duration = 250
                interpolator = AccelerateInterpolator()
                repeatCount = 0
                start()
            }
        }
    }

}