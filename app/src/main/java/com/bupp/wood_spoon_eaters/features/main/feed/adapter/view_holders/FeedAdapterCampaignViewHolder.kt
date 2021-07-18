package com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterCampaignSectionBinding
import com.bupp.wood_spoon_eaters.model.*

class FeedAdapterCampaignViewHolder(val binding: FeedAdapterCampaignSectionBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(item: FeedAdapterCoupons) {
        item.couponSection.items?.let {
            if (it.isNotEmpty()) {
                binding.feedCampaignSectionLayout.visibility = View.VISIBLE

                val adapter = FeedCouponSectionPagerAdapter()
                binding.feedCampaignSectionViewPager.adapter = adapter
                binding.feedCampaignSectionViewPager.setPageTransformer(FeedCampaignCouponItemTransformer())
                adapter.submitList(it)


                if (it.size > 1) {
                    binding.feedCampaignSectionIndicator.visibility = View.VISIBLE
                    binding.feedCampaignSectionIndicator.setViewPager(binding.feedCampaignSectionViewPager)
                }

//                initItemData(0, it)
//
//                binding.feedCampaignSectionViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//                    override fun onPageSelected(position: Int) {
//                        super.onPageSelected(position)
//                        Log.d("wowFeedPager", "onPageSelected - position: $position")
//                        initItemData(position, it, true)
//                    }
//                })


            }
        }
    }
//
//    private fun initItemData(position: Int, data: List<FeedCampaignSectionItem>, animate: Boolean = false) {
//        val currentCoupon = data[position]
//        binding.feedCampaignSectionTitle.text = currentCoupon.title
//        binding.feedCampaignSectionSubTitle.text = currentCoupon.subtitle
//
//        if (animate) {
////            AnimationUtil().enterFromRightWithAlpha(binding.feedCampaignSectionTitle, customStartDelay = 150)
////            AnimationUtil().enterFromRightWithAlpha(binding.feedCampaignSectionSubTitle, customStartDelay = 250)
//        }
//    }
}