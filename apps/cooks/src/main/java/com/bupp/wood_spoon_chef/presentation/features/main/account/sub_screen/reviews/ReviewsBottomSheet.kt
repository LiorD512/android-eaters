package com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.BottomSheetReviewsBinding
import com.bupp.wood_spoon_chef.data.remote.model.CommentAdapterItem
import com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.reviews.ReviewsBSViewModel.ReviewData
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReviewsBottomSheet : TopCorneredBottomSheet() {

    private val viewModel by viewModel<ReviewsBSViewModel>()
    private var binding: BottomSheetReviewsBinding? = null

    val adapter = ReviewsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.bottom_sheet_reviews, container, false)
        binding = BottomSheetReviewsBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreenDialog()

        viewModel.initData()
        initUI()
        initObservers()
    }

    private fun initUI() {
        with(binding!!) {
            reviewBottomSheetReviewList.adapter = adapter
            reviewBottomSheetReviewList.layoutManager = LinearLayoutManager(requireContext())
            reviewBottomSheetHeader.setOnIconClickListener {
                dismiss()
            }
            reviewBottomSheetHeader.setTitle(viewModel.getRestaurantName())
        }
    }

    private fun initObservers(){
        viewModel.commentListData.observe(viewLifecycleOwner) {
            handleReviewList(it)
        }
        viewModel.reviewBreakdown.observe(viewLifecycleOwner) {
            handleReviewBreakdown(it)
        }
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            handleErrorEvent(it, binding?.root)
        }
    }

    private fun handleReviewBreakdown(reviewData: ReviewData?) {
        binding?.apply {
            reviewBottomSheetGraph.initReviewData(reviewData)
            reviewBottomSheetGraph.onToolTipClickListener {
                SatisfactionBottomSheet().show(childFragmentManager,Constants.SATISFACTION_DIALOG)
            }
        }
    }

    private fun handleReviewList(comments: List<CommentAdapterItem>?){
        adapter.submitList(comments)
    }

    override fun clearClassVariables() {
        binding = null
    }

    companion object {
        private const val RESTAURANT_ID = "restaurantId"
        private const val RESTAURANT_NAME = "restaurantName"
        private const val RATING_HEADER = "ratingHeader"
        fun newInstance(restaurantId: Long, restaurantName: String, ratingHeader: String): ReviewsBottomSheet {
            return ReviewsBottomSheet().apply {
                arguments = Bundle().apply {
                    putLong(RESTAURANT_ID, restaurantId)
                    putString(RESTAURANT_NAME, restaurantName)
                    putString(RATING_HEADER, ratingHeader)
                }
            }
        }
    }


}