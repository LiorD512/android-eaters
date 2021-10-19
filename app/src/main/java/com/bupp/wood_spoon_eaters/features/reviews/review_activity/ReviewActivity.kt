package com.bupp.wood_spoon_eaters.features.reviews.review_activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.ActivityReviewBinding
import com.bupp.wood_spoon_eaters.features.reviews.ReviewsViewModel
import com.bupp.wood_spoon_eaters.model.Order
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.bupp.wood_spoon_eaters.R


class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding
    private val viewModel by viewModel<ReviewsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val order = intent.getParcelableExtra<Order>(Constants.ARG_REVIEW)
        viewModel.initExtras(order)

        initObservers()
    }

    private fun initObservers() {
        viewModel.reviewSuccess.observe(this, {
            it.getContentIfNotHandled()?.let { isSuccess ->
                if (isSuccess) {
                    finish()
                }
            }
        })
    }

    override fun onBackPressed() {
        if(findNavController(R.id.reviewsContainerFragment).currentDestination?.label == "fragment_review_experience"){
            viewModel.ignoreTrigger()
        }
        super.onBackPressed()
    }

}