package com.bupp.wood_spoon_eaters.features.main.feed.adapters.view_holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterCampaignSectionBinding
import com.bupp.wood_spoon_eaters.features.main.feed.adapters.FeedCouponSectionPagerAdapter
import com.bupp.wood_spoon_eaters.features.main.feed.adapters.decorators.FeedCampaignCouponItemTransformer
import com.bupp.wood_spoon_eaters.model.FeedAdapterCoupons

class FeedAdapterCampaignViewHolder(val binding: FeedAdapterCampaignSectionBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(item: FeedAdapterCoupons, listener: FeedCouponSectionPagerAdapter.FeedCouponSectionListener) {
        item.couponSection.items?.let {
            if (it.isNotEmpty()) {
                binding.feedCampaignSectionLayout.visibility = View.VISIBLE

                val adapter = FeedCouponSectionPagerAdapter(listener)
                binding.feedCampaignSectionViewPager.adapter = adapter
                binding.feedCampaignSectionViewPager.setPageTransformer(FeedCampaignCouponItemTransformer())
                adapter.submitList(it)


                if (it.size > 1) {
                    binding.feedCampaignSectionIndicator.visibility = View.VISIBLE
                    binding.feedCampaignSectionIndicator.setViewPager(binding.feedCampaignSectionViewPager)
                }
            }
        }
    }
}