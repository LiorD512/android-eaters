package com.bupp.wood_spoon_eaters.bottom_sheets.reviews

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentBottomsheetReviewsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.scope.getOrCreateScope
import org.koin.androidx.viewmodel.ext.android.viewModel

class BottomSheetReviews : BottomSheetDialogFragment() {

    private val viewModel by viewModel<ReviewsBSViewModel>()
    private val binding: FragmentBottomsheetReviewsBinding by viewBinding()

    val adapter = ReviewsAdapter()
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottomsheet_reviews, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetStyle)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_bkg)

        arguments?.let {
            val restaurantId = it.getLong(RESTAURANT_ID,-1)
            val restaurantName = it.getString(RESTAURANT_NAME,"")
            val ratingHeader = it.getString(RATING_HEADER,"")
            viewModel.initData(restaurantId,restaurantName,ratingHeader)
        }

        initUI()
        initObservers()
    }


    private lateinit var behavior: BottomSheetBehavior<View>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)

        }
        return dialog
    }


    private fun initUI() {
        with(binding) {
            reviewBottomSheetReviewList.adapter = adapter
            reviewBottomSheetReviewList.layoutManager = LinearLayoutManager(requireContext())
            reviewBottomSheetCloseIcon.setOnClickListener {
                dismiss()
            }
            reviewBottomSheetTitle.text = viewModel.ratingHeader
            reviewsBottomSheetTextView.text = viewModel.restaurantName
        }
    }

    private fun initObservers(){
        viewModel.commentListData.observe(viewLifecycleOwner,{
            handleReviewList(it)
        })
        viewModel.errorEvent.observe(viewLifecycleOwner,{
            handleErrorEvent()
        })
    }

    private fun handleErrorEvent() {
        with(binding){
            reviewBottomSheetReviewList.isVisible = false
            reviewBottomSheetEmptyLayout.isVisible = true
        }
    }

    private fun handleReviewList(comments: List<CommentAdapterItem>?){
        with(binding){
            adapter.submitList(comments)
        }
    }

    companion object {
        private const val RESTAURANT_ID = "restaurantId"
        private const val RESTAURANT_NAME = "restaurantName"
        private const val RATING_HEADER = "ratingHeader"
        fun newInstance(restaurantId: Long, restaurantName: String, ratingHeader: String): BottomSheetReviews {
            return BottomSheetReviews().apply {
                arguments = Bundle().apply {
                    putLong(RESTAURANT_ID, restaurantId)
                    putString(RESTAURANT_NAME, restaurantName)
                    putString(RATING_HEADER, ratingHeader)
                }
            }
        }
    }


}