package com.bupp.wood_spoon_eaters.features.reviews.review_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.ActivityReviewBinding
import com.bupp.wood_spoon_eaters.features.reviews.ReviewsViewModel
import com.bupp.wood_spoon_eaters.model.Order
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import com.bupp.wood_spoon_eaters.model.WSError
import com.bupp.wood_spoon_eaters.utils.Utils.getErrorsMsg
import com.bupp.wood_spoon_eaters.utils.showErrorToast


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

        viewModel.logEvent(Constants.EVENT_REVIEW_TRIGGERED)
    }

    private fun initObservers() {
        viewModel.reviewSuccess.observe(this, {
            it.getContentIfNotHandled()?.let { isSuccess ->
                if (isSuccess) {
                    finish()
                }
            }
        })
        viewModel.errorEvent.observe(this,{
            handleWSError(it)
        })
        viewModel.progressData.observe(this,{
            handleProgressBar(it)
        })
    }

    private fun handleWSError(errorEvent : LiveEvent<List<WSError>?>) {
        errorEvent.getContentIfNotHandled().let{ errorList->
            val errorMsg = errorList?.getErrorsMsg() ?: "Ops! something went wrong"
            showErrorToast(errorMsg, binding.root)
        }
    }

    private fun handleProgressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.reviewsActProgressBar.show()
        } else {
            binding.reviewsActProgressBar.hide()
        }
    }

    override fun onBackPressed() {
        if(findNavController(R.id.reviewsContainerFragment).currentDestination?.label == "fragment_review_experience"){
            viewModel.ignoreTrigger()
        }
        super.onBackPressed()
    }

}