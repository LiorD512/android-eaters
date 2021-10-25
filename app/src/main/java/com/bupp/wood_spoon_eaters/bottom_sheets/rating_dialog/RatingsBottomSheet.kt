package com.bupp.wood_spoon_eaters.bottom_sheets.rating_dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.Review
import com.bupp.wood_spoon_eaters.databinding.RatingsBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RatingsBottomSheet(val ratings: Review) : BottomSheetDialogFragment() {

    private val binding: RatingsBottomSheetBinding by viewBinding()
    private var adapter: RatingsAdapter? = null

    private lateinit var behavior: BottomSheetBehavior<View>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)
            behavior.isFitToContents = false
            behavior.isDraggable = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.expandedOffset = Utils.toPx(230)
        }

        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTransparentStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.ratings_bottom_sheet, null)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        with(binding){
            ratingsDialogCloseBtn.setOnClickListener { dismiss() }

//            ratingsDialogAccuracyRating.text = "${ratings.accuracyRating}"
//            ratingsDialogDeliveryRating.text = "${ratings.deliveryRating}"
//            ratingsDialogTasteRating.text = "${ratings.dishRating}"

            ratings.comments.let{
                ratingsDialogDishesRecyclerView.layoutManager = LinearLayoutManager(context)
                ratingsDialogDishesRecyclerView.addItemDecoration(RatingItemDecoration())
                adapter = RatingsAdapter(requireContext(), ratings.comments)
                ratingsDialogDishesRecyclerView.adapter = adapter
            }
        }
    }



}