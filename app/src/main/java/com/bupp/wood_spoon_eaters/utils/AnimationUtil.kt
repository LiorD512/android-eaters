package com.bupp.wood_spoon_eaters.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.BounceInterpolator
import com.bupp.wood_spoon_eaters.R
import kotlinx.coroutines.delay

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

    fun enterFromRightWithAlpha(view: View, customDuration: Long = 500, customStartDelay: Long = 150) {
        view.alpha = 0f
        ObjectAnimator.ofFloat(
            view, "translationX",
            250f, 0f,
        ).apply {
            duration = customDuration
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = customStartDelay
            repeatCount = 0
            start()
        }

        ObjectAnimator.ofFloat(
            view, "alpha",
            0f, 1f,
        ).apply {
            duration = customDuration / 2
            interpolator = AccelerateInterpolator()
            startDelay = customStartDelay
            repeatCount = 0
            start()
        }
    }

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

    fun alphaOut(view: View, customDuration: Long = 250, customStartDelay: Long = 150) {
        view.alpha = 1f
        ObjectAnimator.ofFloat(
            view, "alpha",
            1f, 0f,
        ).apply {
            duration = customDuration
            interpolator = AccelerateInterpolator()
            startDelay = customStartDelay
            repeatCount = 0
            start()
        }
    }


}