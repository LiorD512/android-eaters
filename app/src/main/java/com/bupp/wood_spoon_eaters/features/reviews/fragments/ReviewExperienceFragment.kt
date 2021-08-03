package com.bupp.wood_spoon_eaters.features.reviews.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.ActivityReviewBinding
import com.bupp.wood_spoon_eaters.databinding.FragmentReviewExperienceBinding
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import com.bupp.wood_spoon_eaters.features.reviews.ReviewsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReviewExperienceFragment() : Fragment(R.layout.fragment_review_experience) {



    val binding: FragmentReviewExperienceBinding by viewBinding()
    private val viewModel by viewModel<ReviewsViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initUi()
    }

    fun initUi() {
        binding.reviewExperienceFragmentNextBtn.setOnClickListener {
            viewModel.onNextClick()
        }
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(viewLifecycleOwner,  { navigationEvent ->
            handleNavigationEvent(navigationEvent)
        })
    }

    private fun handleNavigationEvent(navigationEvent: LiveEvent<ReviewsViewModel.NavigationEvent>?) {
        navigationEvent?.getContentIfNotHandled()?.let {
            val extras = FragmentNavigatorExtras(binding.image to "logo_transition", binding.title to "title_transition", binding.subtitle to "subtitle_transition")

            when (it) {
                ReviewsViewModel.NavigationEvent.EXPERIENCE_TO_DETAILS -> {
                    val action = ReviewExperienceFragmentDirections.actionReviewExperienceFragmentToReviewDetailsFragment()
                    findNavController().navigate(action,extras)
                }
            }
        }
    }




}