package com.bupp.wood_spoon_eaters.features.main.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.managers.LocationManager
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.fragment_feed.*
import org.koin.android.viewmodel.ext.android.viewModel


class FeedFragment() : Fragment() {
    private val TAG = "wowFeedFragment"
    private val viewModel: FeedViewModel by viewModel<FeedViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO (after setting location manager):
        //1. check previous location
        //2. if has - display and get data; else check permission and get current location (display empty layout)
        FetchLocationAndFeed()
        showEmptyLayout()
    }



    private fun FetchLocationAndFeed() {
        Log.d(TAG, "feed fragment started")
        viewModel.getFeed()
    }

    private fun showEmptyLayout() {
        feedFragEmptyLayout.visibility = View.VISIBLE
        feedFragEmptyFeedTitle.text =  String.format("Hey %s", viewModel.getEaterFirstName()?: "nullStr")
        feedFragEmptyLayout.setOnClickListener { startDeliveryDetails() }
    }

    private fun startDeliveryDetails() {
        (activity as MainActivity).loadDeliveryDetails()

//        val lastLocation = viewModel.getLastLocation()
//        Log.d("wowFeed","lastLocation: ${lastLocation?.latitude}")
    }

}