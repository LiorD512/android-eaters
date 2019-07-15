package com.bupp.wood_spoon_eaters.dialogs.rating_dialog

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.main.search.RatingItemDecoration
import com.bupp.wood_spoon_eaters.model.Review
import kotlinx.android.synthetic.main.ratings_dialog.*
import org.koin.android.viewmodel.ext.android.viewModel

class RatingsDialog : DialogFragment() {

    private lateinit var adapter: RatingsAdapter
    val viewModel by viewModel<RatingsViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.ratings_dialog, null)
        dialog.window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        ratingsDialogCloseBtn.setOnClickListener { dismiss() }

        viewModel.ratingDetails.observe(this, Observer { ratingDetails ->
            if (ratingDetails != null) {
                handleRatingDetails(ratingDetails)
            }
        })

        viewModel.reviewList.observe(this, Observer { reviewList ->
            if (reviewList != null) {
                handleReviewList(reviewList)
            }
        })

        viewModel.getDumbRatingDetails()
        viewModel.getDumbReviewList()
    }

    private fun handleReviewList(reviewList: ArrayList<Review>) {
        ratingsDialogDishesRecyclerView.layoutManager = LinearLayoutManager(context)
        ratingsDialogDishesRecyclerView.addItemDecoration(RatingItemDecoration())
        adapter = RatingsAdapter(context!!, reviewList)
        ratingsDialogDishesRecyclerView.adapter = adapter
    }

    private fun handleRatingDetails(ratingDetails: RatingsViewModel.RatingsDetails) {
        ratingsDialogAvgRating.text = ratingDetails.avgRating.toString()
        ratingsDialogAccuracyRating.text = ratingDetails.accuracyRating.toString()
        ratingsDialogDeliveryRating.text = ratingDetails.deliveryRating.toString()
        ratingsDialogTasteRating.text = ratingDetails.tasteRating.toString()
    }

}