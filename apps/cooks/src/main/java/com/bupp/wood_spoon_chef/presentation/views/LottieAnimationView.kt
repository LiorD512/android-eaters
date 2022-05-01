package com.bupp.wood_spoon_chef.presentation.views

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieDrawable.INFINITE
import com.airbnb.lottie.LottieDrawable.REVERSE
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.LottieAnimationViewBinding


class LottieAnimationView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var binding: LottieAnimationViewBinding = LottieAnimationViewBinding.inflate(LayoutInflater.from(context), this, true)


   init{
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.LottieViewAttrs)
                val isWithBkg = a.getBoolean(R.styleable.LottieViewAttrs_withBkg, false)
                if(!isWithBkg){
                    binding.lottieAnimationViewBkg.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
                }
            if (a.hasValue(R.styleable.LottieViewAttrs_lottieAnimType)) {
                val type = a.getInt(R.styleable.LottieViewAttrs_lottieAnimType, Constants.LOTTIE_ANIM_PB)
                setCustomAnimation(type)
            }
            a.recycle()
        }
    }

    private fun setCustomAnimation(type: Int) {
        when(type){
            Constants.LOTTIE_ANIM_PB -> {
//                binding.lottieAnimationView.setAnimation("loader.json")
                binding.lottieAnimationView.repeatMode = REVERSE
                binding.lottieAnimationView.repeatCount = INFINITE
            }
            Constants.LOTTIE_ANIM_SELECT_ADDRESS -> {
                binding.lottieAnimationView.setAnimation("add_address.json")
                binding.lottieAnimationView.repeatMode = REVERSE
                binding.lottieAnimationView.repeatCount = INFINITE
                binding.lottieAnimationView.scale = 0.6f
            }
            Constants.LOTTIE_ANIM_PAYMENTS -> {
                binding.lottieAnimationView.setAnimation("get_paid_anim.json")
                binding.lottieAnimationView.repeatMode = REVERSE
                binding.lottieAnimationView.repeatCount = INFINITE
                binding.lottieAnimationView.scale = 0.6f
            }
            Constants.LOTTIE_ANIM_NEW_DISH_PRICE -> {
                binding.lottieAnimationView.setAnimation("new_dish_2.json")
                binding.lottieAnimationView.repeatMode = REVERSE
                binding.lottieAnimationView.repeatCount = INFINITE
                binding.lottieAnimationView.scale = 0.2f
            }
            Constants.LOTTIE_ANIM_NEW_DISH_VIDEO -> {
                binding.lottieAnimationView.setAnimation("new_dish_3.json")
                binding.lottieAnimationView.repeatMode = REVERSE
                binding.lottieAnimationView.repeatCount = INFINITE
                binding.lottieAnimationView.scale = 0.6f
            }
            Constants.LOTTIE_ANIM_NEW_DISH_NAME -> {
                binding.lottieAnimationView.setAnimation("new_dish.json")
                binding.lottieAnimationView.repeatMode = REVERSE
                binding.lottieAnimationView.repeatCount = INFINITE
                binding.lottieAnimationView.scale = 1f
            }
        }
        binding.lottieAnimationView.playAnimation()
    }


}