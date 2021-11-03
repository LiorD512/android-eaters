package com.bupp.wood_spoon_eaters.features.reviews.fragments

import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentReviewDetailsBinding
import com.bupp.wood_spoon_eaters.features.reviews.ReviewsViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class ReviewDetailsFragment : Fragment(R.layout.fragment_review_details) {
    var binding: FragmentReviewDetailsBinding? = null
    private val viewModel by sharedViewModel<ReviewsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentReviewDetailsBinding.bind(view)
        initUi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation

        animation.addListener(object : Transition.TransitionListener {
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

    fun initUi() {
        with(binding!!) {
            reviewFragSubmitBtn.setOnClickListener {
                viewModel.onSubmitClick(reviewFragUserReviewInput.getText(),reviewFragUserTeamInput.getText())
            }
            reviewFragExitBtn.setOnClickListener {
                activity?.onBackPressed()
            }
            viewModel.order?.let { order ->
                Glide.with(requireContext()).load(order.restaurant?.thumbnail?.url).placeholder(R.drawable.grey_white_cornered_rect).circleCrop().into(reviewFragImage)
                reviewFragRestName.text = order.restaurant?.restaurantName
                reviewFragCookName.text = " by ${order.restaurant?.getFullName()}"
                reviewFragUserInputText.text = "Hey ${viewModel.getEaterName()}, want to send us a private message?"
            }
        }
    }


    private fun fadeIn() {
        binding!!.reviewFragTitle.let {
            binding!!.reviewFragTitle.apply {
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

        binding!!.reviewFragLayoutReview?.let {
            binding!!.reviewFragLayoutReview.apply {
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}