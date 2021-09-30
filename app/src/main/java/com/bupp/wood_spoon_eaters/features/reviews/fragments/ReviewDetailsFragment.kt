package com.bupp.wood_spoon_eaters.features.reviews.fragments

import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentReviewDetailsBinding
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import com.bupp.wood_spoon_eaters.features.reviews.ReviewsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ReviewDetailsFragment : Fragment(R.layout.fragment_review_details){
    val binding: FragmentReviewDetailsBinding by viewBinding()
    private val viewModel by viewModel<ReviewsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation

        animation.addListener(object  : Transition.TransitionListener{
            override fun onTransitionStart(transition: Transition?) {
            }

            override fun onTransitionEnd(transition: Transition?) {
                fadeIn()
            }

            override fun onTransitionCancel(transition: Transition?) {
            }

            override fun onTransitionPause(transition: Transition?) {
            }

            override fun onTransitionResume(transition: Transition?) {
            }

        })


    }


    private fun  fadeIn() {
        binding.editTextLayoutReview?.let {
            binding.editTextLayoutReview.apply {
                // Set the content view to 0% opacity but visible, so that it is visible
                // (but fully transparent) during the animation.
                alpha = 0f
                visibility = View.VISIBLE

                // Animate the content view to 100% opacity, and clear any animation
                // listener set on the view.
                animate()
                    .alpha(1f)
                    .setDuration(600)

            }
        }

        binding.textViewReview?.let {
            binding.textViewReview.apply {
                // Set the content view to 0% opacity but visible, so that it is visible
                // (but fully transparent) during the animation.
                alpha = 0f
                visibility = View.VISIBLE

                // Animate the content view to 100% opacity, and clear any animation
                // listener set on the view.
                animate()
                    .alpha(1f)
                    .setDuration(600)

            }
        }

    }


}