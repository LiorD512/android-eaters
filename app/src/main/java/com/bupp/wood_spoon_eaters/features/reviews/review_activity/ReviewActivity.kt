package com.bupp.wood_spoon_eaters.features.reviews.review_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.ActivityReviewBinding
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import com.bupp.wood_spoon_eaters.features.reviews.ReviewsViewModel
import com.bupp.wood_spoon_eaters.features.reviews.fragments.ReviewExperienceFragmentDirections
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReviewBinding
    private val viewModel by viewModel<ReviewsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initUi()
        initObservers()
    }

    private fun initUi() {
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this, Observer { navigationEvent ->
            handleNavigationEvent(navigationEvent)
        })
    }

    private fun handleNavigationEvent(navigationEvent: LiveEvent<ReviewsViewModel.NavigationEvent>?) {
        navigationEvent?.getContentIfNotHandled()?.let {
            when (it) {
                ReviewsViewModel.NavigationEvent.EXPERIENCE_TO_DETAILS -> {
                    val action = ReviewExperienceFragmentDirections.actionReviewExperienceFragmentToReviewDetailsFragment()
                    findNavController(R.id.reviewsContainerFragment).navigate(action)
                }
            }
        }
    }
}