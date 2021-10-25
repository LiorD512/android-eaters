package com.bupp.wood_spoon_eaters.features.reviews.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.RatingStarsViewReviews
import com.bupp.wood_spoon_eaters.databinding.FragmentReviewExperienceBinding
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import com.bupp.wood_spoon_eaters.features.reviews.ReviewsViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ReviewExperienceFragment() : Fragment(R.layout.fragment_review_experience), RatingStarsViewReviews.RatingStarsViewListener {

    val binding: FragmentReviewExperienceBinding by viewBinding()
    private val viewModel by sharedViewModel<ReviewsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initUi()
    }

    fun initUi() {
        with(binding) {
            reviewFragRating.setRatingStarsViewListener(this@ReviewExperienceFragment)
            reviewFragNextBtn.setOnClickListener {
                viewModel.onNextClick()
            }
            reviewFragExitBtn.setOnClickListener {
                activity?.finish()
            }
            viewModel.order?.let { order ->
                Glide.with(requireContext()).load(order.restaurant?.thumbnail?.url).placeholder(R.drawable.grey_white_cornered_rect).into(reviewFragImage)
                reviewFragRestName.text = order.restaurant?.restaurantName
                reviewFragCookName.text = order.restaurant?.firstName
            }
        }
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(viewLifecycleOwner, { navigationEvent ->
            handleNavigationEvent(navigationEvent)
        })
    }

    private fun handleNavigationEvent(navigationEvent: LiveEvent<ReviewsViewModel.NavigationEvent>?) {
        navigationEvent?.getContentIfNotHandled()?.let {
            val extras = FragmentNavigatorExtras(
                binding.reviewFragImageLayout to "logo_transition",
                binding.reviewFragImage to "logo_transition2",
                binding.reviewFragRestName to "title_transition",
                binding.reviewFragCookName to "subtitle_transition"
            )

            when (it) {
                ReviewsViewModel.NavigationEvent.EXPERIENCE_TO_DETAILS -> {
                    val action = ReviewExperienceFragmentDirections.actionReviewExperienceFragmentToReviewDetailsFragment()
                    findNavController().navigate(action, extras)
                }
            }
        }
    }

    override fun onRatingClick(rating: Int) {
        viewModel.setRating(rating)
    }

}