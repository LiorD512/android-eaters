package com.bupp.wood_spoon_eaters.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator

class AnimationUtil {


    fun Shake (view: View) :AnimatorSet{
        val animatorSet = AnimatorSet()

        val object1:ObjectAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.05f, 0.90f, 1.05f, 1f)
        val object2:ObjectAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.85f, 1.15f, 0.95f, 1f)

        animatorSet.playTogether(object1, object2)
        return animatorSet
    }

}