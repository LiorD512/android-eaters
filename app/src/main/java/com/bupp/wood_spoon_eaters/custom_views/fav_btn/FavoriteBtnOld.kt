package com.bupp.wood_spoon_eaters.custom_views.fav_btn

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.databinding.FavoriteBtnBinding


class FavoriteBtnOld @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr), FavoriteBtnViewModelOld.FavVMListener {

    var dishId: Long? = null
    val viewModel = FavoriteBtnViewModelOld()
    var isFavSelected = false

    private var binding: FavoriteBtnBinding = FavoriteBtnBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        viewModel.setFavListener(this)
        binding.favBtn.setOnClickListener {
            animateView()
            onClick()
        }
    }

    fun setDishId(dishId: Long) {
        this.dishId = dishId
    }

    override fun onFail() {
        //reverseBtnState
        isFavSelected = !isFavSelected
        updateUi()
    }


    private fun onClick() {
        animateView()
        updateUi()
        viewModel.onClick(dishId, isFavSelected)
    }

    private fun animateView() {
        val anim = ScaleAnimation(
            1f, 1.2f, // Start and end values for the X axis scaling
            1f, 1.2f, // Start and end values for the Y axis scaling
            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
            Animation.RELATIVE_TO_SELF, 0.5f
        ) // Pivot point of Y scaling
//        anim.fillAfter =  // Needed to keep the result of the animation
        anim.duration = 150
        anim.repeatMode = ScaleAnimation.REVERSE
        anim.interpolator = OvershootInterpolator()
        binding.favBtn.startAnimation(anim)
    }

    private fun updateUi() {
        isFavSelected = !isFavSelected
        binding.favBtn.isSelected = isFavSelected
    }

    fun setIsFav(isFavorite: Boolean?) {
        isFavorite?.let{
            isFavSelected = isFavorite
            binding.favBtn.isSelected = isFavorite
        }
    }


}