package com.bupp.wood_spoon_eaters.features.main.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.feed_view.MultiSectionFeedView
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.Feed
import kotlinx.android.synthetic.main.fragment_feed.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class FeedFragment() : Fragment(), MultiSectionFeedView.MultiSectionFeedViewListener {



    private val TAG = "wowFeedFragment"
    private val viewModel: FeedViewModel by viewModel<FeedViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        FetchLocationAndFeed()
        showEmptyLayout()
        initObservers()

    }


    private fun initUi() {
        feedFragSectionsView.setMultiSectionFeedViewListener(this)
    }

    private fun initObservers() {
        viewModel.feedEvent.observe(this, Observer { event ->
            feedFragPb.hide()
            if(event.isSuccess){
                initFeed(event.feedArr!!)
            }
        })
    }

    private fun initFeed(feedArr: ArrayList<Feed>) {
        feedFragEmptyLayout.visibility = View.GONE
        feedFragListLayout.visibility = View.VISIBLE
        feedFragSectionsView.initFeed(feedArr)
    }


    private fun FetchLocationAndFeed() {
        Log.d(TAG, "feed fragment started")
        feedFragPb.show()
        viewModel.getFeed()
    }

    override fun refreshList() {
        FetchLocationAndFeed()
    }

    private fun showEmptyLayout() {
        feedFragEmptyLayout.visibility = View.VISIBLE
        feedFragEmptyFeedTitle.text =  String.format("Hey %s", viewModel.getEaterFirstName()?: "nullStr")
        feedFragEmptyLayout.setOnClickListener { startDeliveryDetails() }
    }

    private fun startDeliveryDetails() {
        (activity as MainActivity).loadDeliveryDetails()

    }

    override fun onDishClick(dish: Dish) {
        (activity as MainActivity).loadNewOrderActivity(dish.menuItem.id)
    }

    override fun onCookClick(cook: Cook) {

    }

}