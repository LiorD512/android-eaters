package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.RatingStarsViewReviewsBinding
import com.bupp.wood_spoon_eaters.utils.Utils


class RatingStarsViewReviews @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var binding: RatingStarsViewReviewsBinding = RatingStarsViewReviewsBinding.inflate(LayoutInflater.from(context), this, true)

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
        attrs.let {
            val attrArray = context.obtainStyledAttributes(attrs, R.styleable.RatingStarsViewReviews)

            with(binding) {
                this@RatingStarsViewReviews.arrayOfStars =
                    arrayListOf<ImageView>(ratingStarNum1, ratingStarNum2, ratingStarNum3, ratingStarNum4, ratingStarNum5)

                ratingStarNum1.setOnClickListener(this@RatingStarsViewReviews)
                ratingStarNum2.setOnClickListener(this@RatingStarsViewReviews)
                ratingStarNum3.setOnClickListener(this@RatingStarsViewReviews)
                ratingStarNum4.setOnClickListener(this@RatingStarsViewReviews)
                ratingStarNum5.setOnClickListener(this@RatingStarsViewReviews)

                reset()


                val isSmallUi = attrArray.getBoolean(R.styleable.RatingStarsViewReviews_isSmall,
                    false)
                if(isSmallUi){
                    setSmallUi()
                }
            }
        }
    }


    private fun setSmallUi(){
        with(binding) {
            val topBottomPadding = Utils.toPx(0)
            val leftRightPadding = Utils.toPx(2)

            ratingStarNum1.setPadding(leftRightPadding,
                topBottomPadding,
                leftRightPadding,
                topBottomPadding)

            ratingStarNum2.setPadding(leftRightPadding,
                topBottomPadding,
                leftRightPadding,
                topBottomPadding)

            ratingStarNum3.setPadding(leftRightPadding,
                topBottomPadding,
                leftRightPadding,
                topBottomPadding)

            ratingStarNum4.setPadding(leftRightPadding,
                topBottomPadding,
                leftRightPadding,
                topBottomPadding)

            ratingStarNum5.setPadding(leftRightPadding,
                topBottomPadding,
                leftRightPadding,
                topBottomPadding)
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