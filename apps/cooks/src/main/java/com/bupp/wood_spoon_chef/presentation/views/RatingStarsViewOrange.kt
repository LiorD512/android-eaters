package com.bupp.wood_spoon_chef.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.RatingStarsViewOrangeBinding


class RatingStarsViewOrange @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var binding: RatingStarsViewOrangeBinding =
        RatingStarsViewOrangeBinding.inflate(LayoutInflater.from(context), this, true)

    private var rating: Double = 0.0
    private var arrayOfStars: ArrayList<ImageView>

    init {
        attrs.let {
            val attrArray =
                context.obtainStyledAttributes(attrs, R.styleable.RatingStarsViewReviews)

            with(binding) {
                this@RatingStarsViewOrange.arrayOfStars =
                    arrayListOf(
                        ratingStarNum1,
                        ratingStarNum2,
                        ratingStarNum3,
                        ratingStarNum4,
                        ratingStarNum5
                    )

                reset()
            }
        }
    }

    fun setRating(rating: Double) {
        reset()
        selectStars(rating)
        this.rating = rating
    }

    private fun selectStars(rating: Double) {
        if (rating != 0.0) {
            for (i in 1 until rating.toInt() + 1) {
                if (rating > i && rating < i + 1) {
                    //half star
                    arrayOfStars[i - 1].isEnabled = true
                } else {
                    //full star
                    arrayOfStars[i - 1].isSelected = true
                }
            }
        }
    }

    private fun reset() {
        for (i in 0..4) {
            //empty star
            arrayOfStars[i].isEnabled = false
            arrayOfStars[i].isEnabled = false
        }
    }

}