package com.bupp.wood_spoon_eaters.views.swipe_for_options_view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.ShareBannerBinding
import com.bupp.wood_spoon_eaters.databinding.SwipeForOptionsViewBinding
import com.bupp.wood_spoon_eaters.model.Campaign
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.FeedCampaignSectionItem


class SwipeForOptionsView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    MotionLayout(context, attrs, defStyleAttr) {

    private var binding: SwipeForOptionsViewBinding = SwipeForOptionsViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun initSwipeableDish(dish: Dish){
        with(binding){
            swipeableDishName.text = dish.name
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

//        binding.swipeableDishMainLayout.setTransitionListener(object: TransitionListener{
//            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {
//                Log.d(TAG, "onTransitionStarted $startId")
//
//            }
//
//            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
//                if(progress > 0.6){
//                    Log.d(TAG, "onTransitionChange $startId progress: $progress")
//                    motionLayout?.clearAnimation()
//                    motionLayout?.transitionToStart()
//                }
//
//            }
//
//            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
//                Log.d(TAG, "onTransitionCompleted $currentId")
//                motionLayout?.clearAnimation()
//                motionLayout?.transitionToStart()
//
//            }
//
//            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {
//                Log.d(TAG, "onTransitionTrigger $triggerId, $positive, $progress")
//            }
//
//        })
    }

    companion object{
        const val TAG = "wowSwipeForOptionsView"
    }



}
