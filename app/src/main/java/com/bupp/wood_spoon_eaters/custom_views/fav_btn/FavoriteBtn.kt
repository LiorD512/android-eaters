package com.bupp.wood_spoon_eaters.custom_views.fav_btn

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.databinding.FavoriteBtnBinding


class FavoriteBtn @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    var isFavSelected = false

    var listener: FavoriteBtnListener? = null

    interface FavoriteBtnListener {
        fun onAddToFavoriteClick()
        fun onRemoveFromFavoriteClick()
    }

    private var binding: FavoriteBtnBinding = FavoriteBtnBinding.inflate(LayoutInflater.from(context), this, true)

    fun setIsFavorite(isFavorite: Boolean?) {
        isFavorite?.let {
            isFavSelected = isFavorite
            binding.favBtn.isSelected = isFavorite
        }
    }

    fun setClickListener(listener: FavoriteBtnListener) {
        this.listener = listener
        binding.favBtn.setOnClickListener {
            onClick()
        }
    }

    fun onFail() {
        //reverseBtnState
        isFavSelected = !isFavSelected
        updateUi()
    }


    private fun onClick() {
        if (isFavSelected) {
            listener?.onRemoveFromFavoriteClick()
        } else {
            listener?.onAddToFavoriteClick()
        }
        animateView()
        updateUi()
    }

    private fun animateView() {
        val anim = ScaleAnimation(
            1f, 1.1f, // Start and end values for the X axis scaling
            1f, 1.1f, // Start and end values for the Y axis scaling
            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
            Animation.RELATIVE_TO_SELF, 0.5f
        ) // Pivot point of Y scaling
        anim.duration = 150
        anim.repeatMode = ScaleAnimation.REVERSE
        anim.interpolator = OvershootInterpolator()
        binding.favBtn.startAnimation(anim)
    }

    private fun updateUi() {
        isFavSelected = !isFavSelected
        binding.favBtn.isSelected = isFavSelected
    }
}