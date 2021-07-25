package com.bupp.wood_spoon_eaters.features.main.abs

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bupp.wood_spoon_eaters.features.main.feed.FeedFragment
import com.bupp.wood_spoon_eaters.features.main.order_history.OrdersHistoryFragment
import com.bupp.wood_spoon_eaters.features.main.profile.my_profile.MyProfileFragment
import com.bupp.wood_spoon_eaters.features.main.search.SearchFragment

class MainActPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        Log.d(TAG, "createFragment: $position")
        return when (position) {
            0 -> {
                FeedFragment()
            }
            1 -> {
                SearchFragment()
            }
            2 -> {
                OrdersHistoryFragment()
            }
            3 -> {
                MyProfileFragment()
            }
            else -> {
                FeedFragment()
            }
        }
    }

    companion object {
        const val NUM_PAGES = 4
        const val TAG = "wowMainActPagerAdapter"
    }
}