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
import com.bupp.wood_spoon_eaters.bottom_sheets.upsale_bottom_sheet.UpSaleAdapterItem
import com.bupp.wood_spoon_eaters.databinding.FragmentBottomsheetReviewsBinding
import com.bupp.wood_spoon_eaters.delete_me.WordAdapter
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.Eater
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetReviews : BottomSheetDialogFragment() {

//    val viewModel by viewModel<ReviewsViewmodel>()
    private val binding: FragmentBottomsheetReviewsBinding by viewBinding()

    val words = arrayListOf("One", "Two", "Three", "Three", "Three", "Three", "Three", "Three", "Three", "Three", "Three", "Three",
        "Three", "Three", "Three", "Three", "Three", "Three", "Three", "Three", "Three", "Three", "Three", "Three", "Three" )


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottomsheet_reviews, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_bkg)

//        val offsetFromTop = 700
//        (dialog as? BottomSheetDialog)?.behavior?.apply {
//            isFitToContents = false
//            expandedOffset = offsetFromTop
//            state = BottomSheetBehavior.STATE_EXPANDED
//        }

        initUI()
    }

    private lateinit var behavior: BottomSheetBehavior<View>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)
//            behavior.isFitToContents = true
//            behavior.isDraggable = false
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.expandedOffset = Utils.toPx(230)
        }
        return dialog
    }


    private fun initUI() {

        with(binding) {
            val adapter = ReviewsAdapter()
            binding.ReviewsList.adapter = adapter
            binding.ReviewsList.layoutManager = LinearLayoutManager(requireContext())
            reviewBottomSheetCloseIcon.setOnClickListener {
                dismiss()
            }

            val list = mutableListOf<ReviewsBaseAdapterItem>()
            list.add(ReviewAdapterTitleItem("bla", 1))
            list.add(ReviewAdapterItem(Comment(313,"Hi there im just cheking if its work...:) ", Eater(12,"0502497343",
            "Karin","Krupetsky","hi",null,null,null,null,0,null,null,null,null,null))))
            list.add(ReviewAdapterItem(Comment(313,"Hi there im just cheking if its work...:) ", Eater(12,"0502497343",
                "Karin","Krupetsky","hi",null,null,null,null,0,null,null,null,null,null))))
            list.add(ReviewAdapterItem(Comment(313,"Hi there im just cheking if its work...:) ", Eater(12,"0502497343",
                "Karin","Krupetsky","hi",null,null,null,null,0,null,null,null,null,null))))
            list.add(ReviewAdapterItem(Comment(313,"Hi there im just cheking if its work...:) ", Eater(12,"0502497343",
                "Karin","Krupetsky","hi",null,null,null,null,0,null,null,null,null,null))))
            list.add(ReviewAdapterItem(Comment(313,"Hi there im just cheking if its work...:) ", Eater(12,"0502497343",
                "Karin","Krupetsky","hi",null,null,null,null,0,null,null,null,null,null))))
            list.add(ReviewAdapterItem(Comment(313,"Hi there im just cheking if its work...:) ", Eater(12,"0502497343",
                "Karin","Krupetsky","hi",null,null,null,null,0,null,null,null,null,null))))
            list.add(ReviewAdapterItem(Comment(313,"Hi there im just cheking if its work...:) ", Eater(12,"0502497343",
                "Karin","Krupetsky","hi",null,null,null,null,0,null,null,null,null,null))))
            list.add(ReviewAdapterItem(Comment(313,"Hi there im just cheking if its work...:) ", Eater(12,"0502497343",
                "Karin","Krupetsky","hi",null,null,null,null,0,null,null,null,null,null))))

//            adapter.submitList(list)
            adapter.submitList(list.subList(0, 5))
            reviewsBottomSheetBtn.setOnClickListener {
              adapter.submitList(list)
            }
        }
    }


}