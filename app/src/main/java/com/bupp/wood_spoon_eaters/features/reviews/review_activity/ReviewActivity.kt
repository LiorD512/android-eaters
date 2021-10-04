package com.bupp.wood_spoon_eaters.features.reviews.review_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.ActivityReviewBinding
import com.bupp.wood_spoon_eaters.features.reviews.ReviewsViewModel
import com.bupp.wood_spoon_eaters.model.Order
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReviewBinding
    private val viewModel by viewModel<ReviewsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val order = intent.getParcelableExtra<Order>(Constants.ARG_REVIEW)
        viewModel.initExtras(order)
    }



}