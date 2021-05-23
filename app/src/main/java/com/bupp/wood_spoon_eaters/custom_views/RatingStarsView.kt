package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.databinding.RatingStarsViewBinding


class RatingStarsView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var binding: RatingStarsViewBinding = RatingStarsViewBinding.inflate(LayoutInflater.from(context), this, true)

    interface RatingStarsViewListener {
        fun onRatingClick()
    }

    fun setRatingStarsViewListener(listener: RatingStarsViewListener) {
        this.listener = listener
    }

    private var numberOfStars: Int = 0
    private var arrayOfStars: ArrayList<ImageView>
    private var listener: RatingStarsViewListener? = null

    init{
        with(binding){
            this@RatingStarsView.arrayOfStars =
                arrayListOf<ImageView>(ratingStarNum1, ratingStarNum2, ratingStarNum3, ratingStarNum4, ratingStarNum5)

            ratingStarNum1.setOnClickListener(this@RatingStarsView)
            ratingStarNum2.setOnClickListener(this@RatingStarsView)
            ratingStarNum3.setOnClickListener(this@RatingStarsView)
            ratingStarNum4.setOnClickListener(this@RatingStarsView)
            ratingStarNum5.setOnClickListener(this@RatingStarsView)

            reset()
        }
    }

    override fun onClick(v: View?) {
        with(binding){
            when (v) {
                ratingStarNum1 -> {
                    setRating(1)
                }
                ratingStarNum2 -> {
                    setRating(2)
                }
                ratingStarNum3 -> {
                    setRating(3)
                }
                ratingStarNum4 -> {
                    setRating(4)
                }
                ratingStarNum5 -> {
                    setRating(5)
                }
            }
            listener?.onRatingClick()
        }
    }

    fun setRating(rating: Int) {
        reset()
        selectStars(rating)

        numberOfStars = rating
    }

    private fun selectStars(numOfStars: Int) {
        if (numOfStars != 0) {
            for (i in 0 until numOfStars) {
                arrayOfStars[i].isSelected = true
            }
        }
    }

    private fun reset() {
        for (i in 0..4) {
            arrayOfStars[i].isSelected = false
        }
    }

    fun getRating(): Int {
        return numberOfStars
    }
}