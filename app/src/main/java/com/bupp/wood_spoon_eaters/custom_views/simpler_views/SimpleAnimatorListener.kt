package com.bupp.wood_spoon_eaters.custom_views.simpler_views

import android.animation.Animator

abstract class SimpleAnimatorListener: Animator.AnimatorListener{

    override fun onAnimationCancel(p0: Animator?) {}

    override fun onAnimationEnd(p0: Animator?) {}

    override fun onAnimationRepeat(p0: Animator?) {}

    override fun onAnimationStart(p0: Animator?) {}
}