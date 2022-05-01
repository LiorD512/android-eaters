package com.bupp.wood_spoon_chef.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.ReviewsGraphBinding
import com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.reviews.ReviewsBSViewModel.ReviewData


class ReviewsGraphView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr){

    private var binding: ReviewsGraphBinding = ReviewsGraphBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initUi(attrs)
    }

    private fun initUi(attrs: AttributeSet?) {
        attrs?.let {
            val attr = context.obtainStyledAttributes(attrs, R.styleable.ReviewsGraphView)
            attr.recycle()
        }
    }

    fun initReviewData(reviewData: ReviewData?){
        handleGraphUi(reviewData)
    }

    fun onToolTipClickListener(function:() -> Unit){
        binding.graphAvgRating.setOnClickListener{
            function.invoke()
        }
    }

    private fun handleGraphUi(reviewData: ReviewData?) {
        with(binding){
            graphAvgRating.text = reviewData?.avgRating?.toString()
            graphAvgReviewCount.text = "${reviewData?.reviewCount} Reviews"

            reviewData?.breakdown?.let{ breakdown->
                graph1StarPB.progress = breakdown.countOf1
                graph2StarPB.progress = breakdown.countOf2
                graph3StarPB.progress = breakdown.countOf3
                graph4StarPB.progress = breakdown.countOf4
                graph5StarPB.progress = breakdown.countOf5
            }

            graphAvgReviewStarts.setRating(reviewData?.avgRating ?: 0.0)
        }
    }


    companion object {
        const val TAG = "wowTitleBodyView"
    }


}
