package com.bupp.wood_spoon_eaters.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import com.bupp.wood_spoon_eaters.R

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

    fun scaleToWrapContent(context: Context, view: View) {
        val animShow = AnimationUtils.loadAnimation(context, R.anim.view_show)
        view.visibility = View.VISIBLE
        view.startAnimation(animShow)
//        ObjectAnimator.ofFloat(
//            view, "translationY",
//            0f, 1f,
//        ).apply {
//            duration = 500
//            interpolator = AccelerateDecelerateInterpolator()
//            repeatCount = 0
//            start()
//        }
    }

    fun scaleToZero(context: Context, view: View) {
        val animHide = AnimationUtils.loadAnimation(context, R.anim.view_hide)
        view.visibility = View.GONE
        view.startAnimation(animHide)
//        ObjectAnimator.ofFloat(
//            view, "translationY",
//            1f, 0f,
//        ).apply {
//            duration = 500
//            interpolator = AccelerateDecelerateInterpolator()
//            repeatCount = 0
//            start()
//        }
    }

}