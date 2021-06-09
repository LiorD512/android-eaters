package com.bupp.wood_spoon_eaters.bottom_sheets.rating_dialog

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.RatingsBottomSheetBinding
import com.bupp.wood_spoon_eaters.model.Review
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RatingsBottomSheet(val ratings: Review) : BottomSheetDialogFragment() {

    private lateinit var binding: RatingsBottomSheetBinding
    private lateinit var adapter: RatingsAdapter


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

        binding = RatingsBottomSheetBinding.bind(view)

//        val parent = view.parent as View
//        parent.setBackgroundResource(R.drawable.bottom_sheet_bkg)

        initUi()
    }

    private fun initUi() {
        with(binding){
            ratingsDialogCloseBtn.setOnClickListener { dismiss() }

            ratingsDialogAccuracyRating.text = "${ratings.accuracyRating}"
            ratingsDialogDeliveryRating.text = "${ratings.deliveryRating}"
            ratingsDialogTasteRating.text = "${ratings.dishRating}"

            ratings.comments?.let{
                ratingsDialogDishesRecyclerView.layoutManager = LinearLayoutManager(context)
                ratingsDialogDishesRecyclerView.addItemDecoration(RatingItemDecoration())
                adapter = RatingsAdapter(requireContext(), ratings.comments)
                ratingsDialogDishesRecyclerView.adapter = adapter
            }
        }
    }



}