package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieDrawable.INFINITE
import com.airbnb.lottie.LottieDrawable.REVERSE
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.LottieAnimationViewBinding


class LottieAnimationView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var binding: LottieAnimationViewBinding = LottieAnimationViewBinding.inflate(LayoutInflater.from(context), this, true)

   init{
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.LottieViewAttrs)
                var isWithBkg = a.getBoolean(R.styleable.LottieViewAttrs_withBkg, false)
                if(!isWithBkg){
                    binding.lottieAnimationViewBkg.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
                }
            if (a.hasValue(R.styleable.LottieViewAttrs_lottieAnimType)) {
                var type = a.getInt(R.styleable.LottieViewAttrs_lottieAnimType, Constants.LOTTIE_ANIM_PB)
                setCustomAnimation(type)
            }
            a.recycle()
        }
    }

    private fun setCustomAnimation(type: Int) {
        when(type){
            Constants.LOTTIE_ANIM_PB -> {
                binding.lottieAnimationView.setAnimation("dish_loader.json")
                binding.lottieAnimationView.repeatMode = REVERSE
                binding.lottieAnimationView.repeatCount = INFINITE
            }
            Constants.LOTTIE_ANIM_SELECT_ADDRESS -> {
                binding.lottieAnimationView.setAnimation("select_address.json")
                binding.lottieAnimationView.repeatMode = REVERSE
                binding.lottieAnimationView.repeatCount = INFINITE
                binding.lottieAnimationView.scale = 0.6f
            }
            Constants.LOTTIE_ANIM_LOCATION_PERMISSION -> {
                binding.lottieAnimationView.setAnimation("location_permission_anim.json")
                binding.lottieAnimationView.repeatMode = REVERSE
                binding.lottieAnimationView.repeatCount = INFINITE
                binding.lottieAnimationView.scale = 0.6f
            }
            Constants.LOTTIE_ANIM_EMPTY_FEED -> {
                binding.lottieAnimationView.setAnimation("empty_feed.json")
                binding.lottieAnimationView.repeatCount = INFINITE
                binding.lottieAnimationView.scale = 0.6f
            }
        }
        binding.lottieAnimationView.playAnimation()
    }


}