package com.bupp.wood_spoon_eaters.custom_views.simpler_views

import androidx.constraintlayout.motion.widget.MotionLayout

abstract class SimpleMotionTransitionListener: MotionLayout.TransitionListener{

    override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) { }

    override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) { }

    override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) { }

    override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) { }
}