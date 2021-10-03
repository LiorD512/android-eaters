package com.bupp.wood_spoon_eaters.features.reviews.review_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bupp.wood_spoon_eaters.databinding.ActivityReviewBinding

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
    }

    private fun initUi() {
    }


}