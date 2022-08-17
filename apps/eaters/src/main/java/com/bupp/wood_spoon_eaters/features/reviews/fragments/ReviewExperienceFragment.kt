package com.bupp.wood_spoon_eaters.features.reviews.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.RatingStarsViewReviews
import com.bupp.wood_spoon_eaters.databinding.FragmentReviewExperienceBinding
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import com.bupp.wood_spoon_eaters.features.reviews.ReviewsViewModel
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ReviewExperienceFragment() : Fragment(R.layout.fragment_review_experience),
    RatingStarsViewReviews.RatingStarsViewListener {

    var binding: FragmentReviewExperienceBinding? = null
    private val viewModel by sharedViewModel<ReviewsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentReviewExperienceBinding.bind(view)

        initObservers()
        initUi()
    }

    fun initUi() {
        with(binding!!) {
            reviewFragRating.setRatingStarsViewListener(this@ReviewExperienceFragment)
            reviewFragNextBtn.setOnClickListener {
                viewModel.onNextClick()
            }
            reviewFragExitBtn.setOnClickListener {
                viewModel.logEvent(Constants.EVENT_REVIEW_EXIT_1_PAGE)
                activity?.onBackPressed()
            }
            viewModel.order?.let { order ->
                Glide.with(requireContext()).load(order.restaurant?.thumbnail?.url)
                    .placeholder(R.drawable.grey_white_cornered_rect).circleCrop()
                    .into(reviewFragImage)
                reviewFragRestName.text = order.restaurant?.restaurantName
                reviewFragCookName.text = " by ${order.restaurant?.getFullName()}"
            }
        }
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(viewLifecycleOwner) { navigationEvent ->
            handleNavigationEvent(navigationEvent)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.showAppReviewFlow.collect {
                        if (it) {
                            showAppReview()
                        }
                    }
                }
            }
        }
    }

    private fun showAppReview() {
        ReviewManagerFactory.create(requireContext()).apply {
            requestReviewFlow().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    launchReviewFlow(requireActivity(), reviewInfo)
                }
            }
        }
    }

    private fun handleNavigationEvent(navigationEvent: LiveEvent<ReviewsViewModel.NavigationEvent>?) {
        navigationEvent?.getContentIfNotHandled()?.let {
            val extras = FragmentNavigatorExtras(
                binding!!.reviewFragImageLayout to "logo_transition",
                binding!!.reviewFragImage to "logo_transition2",
                binding!!.reviewFragRestName to "title_transition",
                binding!!.reviewFragCookName to "subtitle_transition"
            )

            when (it) {
                ReviewsViewModel.NavigationEvent.EXPERIENCE_TO_DETAILS -> {
                    val action =
                        ReviewExperienceFragmentDirections.actionReviewExperienceFragmentToReviewDetailsFragment()
                    findNavController().navigate(action, extras)
                }
            }
        }
    }

    override fun onRatingClick(rating: Int) {
        viewModel.setRating(rating)
        viewModel.showAppReviewIfPossible()
    }


}