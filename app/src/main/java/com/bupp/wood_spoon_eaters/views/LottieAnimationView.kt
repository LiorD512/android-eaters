package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieDrawable.INFINITE
import com.airbnb.lottie.LottieDrawable.REVERSE
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.LottieAnimationViewBinding
import com.bupp.wood_spoon_eaters.databinding.MapHeaderViewLayoutBinding
import kotlinx.android.synthetic.main.lottie_animation_view.view.*


class LottieAnimationView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var binding: LottieAnimationViewBinding = LottieAnimationViewBinding.inflate(LayoutInflater.from(context), this, true)


    private lateinit var listener: LottieAnimListener

    interface LottieAnimListener{
        fun onAnimationEnd()
    }

   init{
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.LottieViewAttrs)
                var isWithBkg = a.getBoolean(R.styleable.LottieViewAttrs_withBkg, false)
                if(!isWithBkg){
                    lottieAnimationViewBkg.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
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
        }
        binding.lottieAnimationView.playAnimation()
    }

    fun showDefaultAnimation(listener: LottieAnimListener) {
        this.listener = listener
        lottieAnimationView.setAnimation("splash_anim.json")
        lottieAnimationView.playAnimation()

        Handler().postDelayed({
//            Log.d("wowLottie","onAnimationEnd")
            lottieAnimationView.pauseAnimation()
            listener.onAnimationEnd()
        }, 3200)

    }

    fun rollAnimation(){
//        Log.d("wowLottie","rollAnimation: isAnimationg: ${lottieAnimationView.isAnimating}")
        if(!lottieAnimationView.isAnimating){
            lottieAnimationView.playAnimation()

            Handler().postDelayed({
//                Log.d("wowLottie","onAnimationEnd")
                lottieAnimationView.pauseAnimation()
                listener.onAnimationEnd()
            }, 3200)
        }
    }

    fun isAnimating(): Boolean{
        return lottieAnimationView.isAnimating
    }

//    fun setCustomAnimation(animation: String?) {
//        if(animation != null){
//            lottieAnimationView.setAnimationFromJson(animation, "${Date().time}")
//            lottieAnimationView.playAnimation()
//        }else{
//            showDefaultAnimation()
//        }
//    }


}