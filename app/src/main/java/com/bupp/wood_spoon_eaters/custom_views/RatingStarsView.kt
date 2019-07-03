package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.price_range_view.view.*
import kotlinx.android.synthetic.main.rating_stars_view.view.*


class RatingStarsView : LinearLayout, View.OnClickListener {

    interface RatingStarsViewListener {
        fun onRatingClick()
    }

    fun setRatingStarsViewListener(listener: RatingStarsViewListener) {
        this.listener = listener
    }

    private var numberOfStars: Int = 0
    private var arrayOfStars: ArrayList<ImageView>
    private var listener: RatingStarsViewListener? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.rating_stars_view, this, true)

        this.arrayOfStars =
            arrayListOf<ImageView>(ratingStarNum1, ratingStarNum2, ratingStarNum3, ratingStarNum4, ratingStarNum5)

        ratingStarNum1.setOnClickListener(this)
        ratingStarNum2.setOnClickListener(this)
        ratingStarNum3.setOnClickListener(this)
        ratingStarNum4.setOnClickListener(this)
        ratingStarNum5.setOnClickListener(this)

        reset()
    }

    override fun onClick(v: View?) {
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