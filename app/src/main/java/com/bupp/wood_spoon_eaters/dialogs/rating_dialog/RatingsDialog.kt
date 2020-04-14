package com.bupp.wood_spoon_eaters.dialogs.rating_dialog

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Review
import kotlinx.android.synthetic.main.ratings_dialog.*

class RatingsDialog(val ratings: Review) : DialogFragment() {

    private lateinit var adapter: RatingsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.ratings_dialog, null)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        ratingsDialogCloseBtn.setOnClickListener { dismiss() }

        ratingsDialogAccuracyRating.text = "${ratings.accuracyRating}"
        ratingsDialogDeliveryRating.text = "${ratings.deliveryRating}"
        ratingsDialogTasteRating.text = "${ratings.dishRating}"

        if(ratings.comments != null){
            ratingsDialogDishesRecyclerView.layoutManager = LinearLayoutManager(context)
            ratingsDialogDishesRecyclerView.addItemDecoration(RatingItemDecoration())
            adapter = RatingsAdapter(context!!, ratings.comments)
            ratingsDialogDishesRecyclerView.adapter = adapter
        }
    }



}