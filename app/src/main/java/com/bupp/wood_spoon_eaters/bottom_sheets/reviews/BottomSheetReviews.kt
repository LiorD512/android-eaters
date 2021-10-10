package com.bupp.wood_spoon_eaters.bottom_sheets.reviews

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentBottomsheetReviewsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetReviews : BottomSheetDialogFragment() {

    private val binding: FragmentBottomsheetReviewsBinding by viewBinding()

    private var restaurantName: String = ""
    private var ratingHeader: String = ""
    private var review: Review? = null

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
            review = it.getParcelable<Review>(REVIEW)
            restaurantName = it.getString(RESTAURANT_NAME,"")
            ratingHeader = it.getString(RATING_HEADER,"")
        }

        initUI()
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
            val adapter = ReviewsAdapter()
            ReviewsList.adapter = adapter
            ReviewsList.layoutManager = LinearLayoutManager(requireContext())
            reviewBottomSheetCloseIcon.setOnClickListener {
                dismiss()
            }
            reviewBottomSheetTitle.text = ratingHeader
            review?.let { review ->
                reviewsBottomSheetTextView.text = restaurantName
                val list = mutableListOf<Comment>()
                list.addAll(review.comments)
                adapter.submitList(list)
            }
        }
    }

    companion object {
        private const val REVIEW = "review"
        private const val RESTAURANT_NAME = "restaurantName"
        private const val RATING_HEADER = "ratingHeader"
        fun newInstance(review: Review, restaurantName: String, ratingHeader: String): BottomSheetReviews {
            return BottomSheetReviews().apply {
                arguments = Bundle().apply {
                    putParcelable(REVIEW, review)
                    putString(RESTAURANT_NAME, restaurantName)
                    putString(RATING_HEADER, ratingHeader)
                }
            }
        }
    }


}