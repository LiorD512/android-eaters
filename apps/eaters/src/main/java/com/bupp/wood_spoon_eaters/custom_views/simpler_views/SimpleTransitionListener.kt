package com.bupp.wood_spoon_eaters.custom_views.simpler_views

import android.transition.Transition

abstract class SimpleTransitionListener: Transition.TransitionListener{

    override fun onTransitionCancel(p0: Transition?) {}

    override fun onTransitionEnd(p0: Transition?) {}

    override fun onTransitionPause(p0: Transition?) {}

    override fun onTransitionResume(p0: Transition?) {}

    override fun onTransitionStart(p0: Transition?) {}
}