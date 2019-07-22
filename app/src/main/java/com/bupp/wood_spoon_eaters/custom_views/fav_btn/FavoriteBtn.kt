package com.bupp.wood_spoon_eaters.custom_views.fav_btn

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import com.example.matthias.mvvmcustomviewexample.custom.FavoriteBtnViewModel
import kotlinx.android.synthetic.main.favorite_btn.view.*


class FavoriteBtn : FrameLayout, FavoriteBtnViewModel.FavVMListener {

    var dishId: Long? = null
    val viewModel = FavoriteBtnViewModel()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.favorite_btn, this, true)

        viewModel.setFavListener(this)
        favBtn.setOnClickListener {
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

    var isFavSelected = false

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
        favBtn.startAnimation(anim)
    }

    private fun updateUi() {
        isFavSelected = !isFavSelected
        favBtn.isSelected = isFavSelected
    }

    fun setIsFav(isFavorite: Boolean) {
        favBtn.isSelected = isFavorite
    }


}