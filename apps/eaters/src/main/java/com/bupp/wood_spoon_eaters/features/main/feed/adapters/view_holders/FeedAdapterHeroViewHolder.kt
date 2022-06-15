package com.bupp.wood_spoon_eaters.features.main.feed.adapters.view_holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterHeroSectionBinding
import com.bupp.wood_spoon_eaters.features.main.feed.adapters.decorators.FeedCampaignCouponItemTransformer
import com.bupp.wood_spoon_eaters.model.FeedAdapterHero

class FeedAdapterHeroViewHolder(
    val binding: FeedAdapterHeroSectionBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(
        item: FeedAdapterHero,
        listener: FeedHeroSectionPagerAdapter.FeedHeroSectionListener
    ) {
        val adapter = FeedHeroSectionPagerAdapter(listener)

        binding.feedHeroSectionViewPager.adapter = adapter
        binding.feedHeroSectionViewPager.setPageTransformer(FeedCampaignCouponItemTransformer())
        adapter.submitList(item.heroList)

        if (item.heroList.size > 1) {
            binding.feedHeroSectionIndicator.visibility = View.VISIBLE
            binding.feedHeroSectionIndicator.setViewPager(binding.feedHeroSectionViewPager)
        } else {
            binding.feedHeroSectionIndicator.visibility = View.GONE
        }
    }
}