package com.bupp.wood_spoon_eaters.custom_views

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.lottie_animation_view.view.*
import java.util.*


class LottieAnimationView : FrameLayout {

    interface LottieAnimListener{
        fun onAnimationEnd()
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.lottie_animation_view, this, true)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.LottieViewAttrs)
                var isWithBkg = a.getBoolean(R.styleable.LottieViewAttrs_withBkg, false)
                if(!isWithBkg){
                    lottieAnimationViewBkg.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
                }
            a.recycle()
        }
//        showDefaultAnimation()
    }

    fun showDefaultAnimation(listener: LottieAnimListener) {
        lottieAnimationView.setAnimation("splash_anim.json")
        lottieAnimationView.playAnimation()

        lottieAnimationView.addAnimatorListener(object: Animator.AnimatorListener{
            override fun onAnimationRepeat(p0: Animator?) {
                Log.d("wowLottie","onAnimationRepeat")
            }

            override fun onAnimationEnd(p0: Animator?) {
                Log.d("wowLottie","onAnimationEnd")
                listener.onAnimationEnd()
            }

            override fun onAnimationCancel(p0: Animator?) {
                Log.d("wowLottie","onAnimationCancel")
            }

            override fun onAnimationStart(p0: Animator?) {
                Log.d("wowLottie","onAnimationStart")
            }

        })
    }

    fun rollAnimation(){
        lottieAnimationView.playAnimation()
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