package com.bupp.wood_spoon_eaters.features.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.main.delivery_details.DeliveryDetailsFragment
import com.bupp.wood_spoon_eaters.features.main.feed.FeedFragment
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadDeliveryDetails()
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainActContainer, fragment, tag)
            .commit()
    }

    private fun loadFeed() {
        loadFragment(FeedFragment(), Constants.FEED_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_FEED)
    }

    fun loadDeliveryDetails() {
        loadFragment(DeliveryDetailsFragment.newInstance(), Constants.DELIVERY_DETAILS_TAG)
    }

    fun loadLocationChooser() {
//        loadFragment()
    }

    fun loadDeliveryDetailsSetup() {
        
    }


}